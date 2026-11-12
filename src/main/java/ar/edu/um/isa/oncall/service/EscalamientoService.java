package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.CorrelacionCambioIncidente;
import ar.edu.um.isa.oncall.domain.DespliegueDeServicio;
import ar.edu.um.isa.oncall.domain.EjecucionDeEscalamiento;
import ar.edu.um.isa.oncall.domain.EventoDeIncidente;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.Notificacion;
import ar.edu.um.isa.oncall.domain.PasoEscalamiento;
import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import ar.edu.um.isa.oncall.domain.User;
import ar.edu.um.isa.oncall.domain.enumeration.Canal;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoCorrelacion;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoNotificacion;
import ar.edu.um.isa.oncall.domain.enumeration.MotivoDestinatario;
import ar.edu.um.isa.oncall.domain.enumeration.OrigenCorrelacion;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoDespliegue;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoEscalamiento;
import ar.edu.um.isa.oncall.domain.enumeration.TipoEvento;
import ar.edu.um.isa.oncall.repository.CorrelacionCambioIncidenteRepository;
import ar.edu.um.isa.oncall.repository.EscalamientoRepository;
import ar.edu.um.isa.oncall.repository.EventoDeIncidenteRepository;
import ar.edu.um.isa.oncall.repository.NotificacionRepository;
import ar.edu.um.isa.oncall.service.dto.EscalonDTO;
import ar.edu.um.isa.oncall.service.dto.EstadoDeCorteDTO;
import ar.edu.um.isa.oncall.service.dto.ResultadoEscalamientoDTO;
import ar.edu.um.isa.oncall.service.dto.ResultadoEscalamientoDTO.Accion;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Cada llamada a evaluar() mira un incidente y hace a lo sumo un paso de la
 * cadena. La memoria de por donde va esta en EjecucionDeEscalamiento.
 *
 */
@Service
@Transactional
public class EscalamientoService {

    public static final int NIVEL_AUTOR = 0;

    private final EscalamientoRepository escalamientoRepository;
    private final NotificacionRepository notificacionRepository;
    private final EventoDeIncidenteRepository eventoDeIncidenteRepository;
    private final CorrelacionCambioIncidenteRepository correlacionRepository;
    private final GuardiaVigenteService guardiaVigenteService;
    private final Clock clock;
    private final int ventanaCorrelacionMinutos;
    private final int esperaAutorMinutos;

    public EscalamientoService(
        EscalamientoRepository escalamientoRepository,
        NotificacionRepository notificacionRepository,
        EventoDeIncidenteRepository eventoDeIncidenteRepository,
        CorrelacionCambioIncidenteRepository correlacionRepository,
        GuardiaVigenteService guardiaVigenteService,
        Clock clock,
        @Value("${oncall.escalamiento.ventana-correlacion-minutos:120}") int ventanaCorrelacionMinutos,
        @Value("${oncall.escalamiento.espera-autor-minutos:5}") int esperaAutorMinutos
    ) {
        this.escalamientoRepository = escalamientoRepository;
        this.notificacionRepository = notificacionRepository;
        this.eventoDeIncidenteRepository = eventoDeIncidenteRepository;
        this.correlacionRepository = correlacionRepository;
        this.guardiaVigenteService = guardiaVigenteService;
        this.clock = clock;
        this.ventanaCorrelacionMinutos = ventanaCorrelacionMinutos;
        this.esperaAutorMinutos = esperaAutorMinutos;
    }

    @Transactional(readOnly = true)
    public List<Long> incidentesAEvaluar() {
        return escalamientoRepository.idsDeIncidentesEnEstado(EstadoIncidente.ABIERTO);
    }

    public Optional<ResultadoEscalamientoDTO> evaluar(Long incidenteId) {
        Instant ahora = clock.instant();
        return escalamientoRepository.bloquearIncidente(incidenteId).map(incidente -> evaluar(incidente, ahora));
    }

    private ResultadoEscalamientoDTO evaluar(Incidente incidente, Instant ahora) {
        // 1. Estados de corte: si alguien ya se hizo cargo, no se escala.
        if (incidente.getEstado() != EstadoIncidente.ABIERTO) {
            return resultado(
                incidente,
                ahora,
                Accion.CORTADO,
                "El incidente esta " + incidente.getEstado() + ": no se escala",
                List.of(),
                null
            );
        }
        if (incidente.getSilenciadoHasta() != null && incidente.getSilenciadoHasta().isAfter(ahora)) {
            return resultado(
                incidente,
                ahora,
                Accion.CORTADO,
                "Silenciado hasta " + incidente.getSilenciadoHasta(),
                List.of(),
                incidente.getSilenciadoHasta()
            );
        }

        // 2. Memoria: el ultimo escalon dice si ya termino o si todavia hay que
        // esperar.
        Optional<EjecucionDeEscalamiento> ultima = escalamientoRepository.findFirstByIncidenteIdOrderByIdDesc(incidente.getId());
        if (ultima.isPresent()) {
            Instant proximo = ultima.orElseThrow().getProximoEn();
            if (proximo == null) {
                return resultado(
                    incidente,
                    ahora,
                    Accion.TERMINADO,
                    "La cadena ya termino: " + ultima.orElseThrow().getResultado(),
                    List.of(),
                    null
                );
            }
            if (proximo.isAfter(ahora)) {
                return resultado(incidente, ahora, Accion.EN_ESPERA, "El proximo escalon toca a las " + proximo, List.of(), proximo);
            }
        }

        // 3. Politica: la del servicio mas critico entre los afectados.
        List<PoliticaEscalamiento> politicas = escalamientoRepository.politicasDelIncidente(incidente.getId());
        List<PasoEscalamiento> pasos = politicas.isEmpty() ? List.of() : escalamientoRepository.pasosDePolitica(politicas.get(0).getId());
        if (pasos.isEmpty()) {
            EjecucionDeEscalamiento sinPolitica = registrar(
                incidente,
                0,
                0,
                null,
                null,
                MotivoDestinatario.NINGUNO,
                ResultadoEscalamiento.SIN_POLITICA,
                ahora,
                null,
                "Ningun servicio afectado tiene una politica de escalamiento con pasos"
            );
            return resultado(incidente, ahora, Accion.TERMINADO, sinPolitica.getDetalle(), List.of(EscalonDTO.de(sinPolitica)), null);
        }
        PoliticaEscalamiento politica = politicas.get(0);
        int repetirVeces = politica.getRepetirVeces() != null ? politica.getRepetirVeces() : 0;

        // Los pasos que comparten "orden" forman un mismo nivel y se ejecutan juntos.
        NavigableMap<Integer, List<PasoEscalamiento>> niveles = pasos
            .stream()
            .collect(Collectors.groupingBy(PasoEscalamiento::getOrden, TreeMap::new, Collectors.toList()));

        // 4. Primera evaluacion: vuelta de tuerca, primero el autor del cambio.
        if (ultima.isEmpty()) {
            Optional<User> autor = autorDelCambioSospechoso(incidente, ahora);
            if (autor.isPresent()) {
                Canal canal = niveles.firstEntry().getValue().get(0).getCanal();
                Instant proximo = ahora.plus(esperaAutorMinutos, ChronoUnit.MINUTES);
                EjecucionDeEscalamiento aviso = notificar(
                    incidente,
                    0,
                    NIVEL_AUTOR,
                    null,
                    autor.orElseThrow(),
                    MotivoDestinatario.AUTOR_DEL_CAMBIO,
                    canal,
                    ahora,
                    proximo,
                    "Autor del cambio correlacionado con el incidente"
                );
                return resultado(
                    incidente,
                    ahora,
                    Accion.ESCALADO,
                    "Aviso al autor del cambio antes que a la guardia",
                    List.of(EscalonDTO.de(aviso)),
                    proximo
                );
            }
            return ejecutarNivel(incidente, 0, niveles.firstKey(), niveles, ahora);
        }

        // 5. Siguiente escalon: el nivel que sigue, o una vuelta nueva, o agotado.
        EjecucionDeEscalamiento anterior = ultima.orElseThrow();
        int vuelta = anterior.getVuelta();
        Integer siguiente = anterior.getNivel() == NIVEL_AUTOR ? niveles.firstKey() : niveles.higherKey(anterior.getNivel());
        if (siguiente == null) {
            if (vuelta < repetirVeces) {
                vuelta++;
                siguiente = niveles.firstKey();
            } else {
                EjecucionDeEscalamiento agotado = registrar(
                    incidente,
                    vuelta,
                    anterior.getNivel(),
                    null,
                    null,
                    MotivoDestinatario.NINGUNO,
                    ResultadoEscalamiento.AGOTADO,
                    ahora,
                    null,
                    "Se recorrieron todos los niveles " + (repetirVeces + 1) + " vez/veces y nadie reconocio el incidente"
                );
                return resultado(incidente, ahora, Accion.TERMINADO, agotado.getDetalle(), List.of(EscalonDTO.de(agotado)), null);
            }
        }
        return ejecutarNivel(incidente, vuelta, siguiente, niveles, ahora);
    }

    private ResultadoEscalamientoDTO ejecutarNivel(
        Incidente incidente,
        int vuelta,
        int nivel,
        NavigableMap<Integer, List<PasoEscalamiento>> niveles,
        Instant ahora
    ) {
        List<PasoEscalamiento> pasosDelNivel = niveles.get(nivel);
        int esperaMinutos = pasosDelNivel.stream().mapToInt(PasoEscalamiento::getEsperaMinutos).max().orElse(0);
        Instant proximo = ahora.plus(esperaMinutos, ChronoUnit.MINUTES);

        List<EscalonDTO> ejecutados = new ArrayList<>();
        for (PasoEscalamiento paso : pasosDelNivel) {
            Destinatario destinatario = resolverDestinatario(paso, ahora);
            EjecucionDeEscalamiento escalon =
                destinatario.usuario() != null
                    ? notificar(
                          incidente,
                          vuelta,
                          nivel,
                          paso,
                          destinatario.usuario(),
                          destinatario.motivo(),
                          paso.getCanal(),
                          ahora,
                          proximo,
                          destinatario.detalle()
                      )
                    : registrar(
                          incidente,
                          vuelta,
                          nivel,
                          paso,
                          null,
                          MotivoDestinatario.NINGUNO,
                          ResultadoEscalamiento.SIN_DESTINATARIO,
                          ahora,
                          proximo,
                          destinatario.detalle()
                      );
            ejecutados.add(EscalonDTO.de(escalon));
        }
        return resultado(incidente, ahora, Accion.ESCALADO, "Vuelta " + vuelta + ", nivel " + nivel, ejecutados, proximo);
    }

    private record Destinatario(User usuario, MotivoDestinatario motivo, String detalle) {}

    /**
     * A quien avisa un paso: el destinatario directo si lo tiene; si no, quien
     * este de guardia en su rotacion (regla C minima); si no hay nadie, un hueco.
     */
    private Destinatario resolverDestinatario(PasoEscalamiento paso, Instant ahora) {
        if (paso.getDestinatarioDirecto() != null) {
            return new Destinatario(
                paso.getDestinatarioDirecto(),
                MotivoDestinatario.DESTINATARIO_DIRECTO,
                "Destinatario directo del paso " + paso.getOrden()
            );
        }
        if (paso.getRotacion() != null) {
            String rotacion = paso.getRotacion().getNombre();
            return guardiaVigenteService
                .turnoVigente(paso.getRotacion().getId(), ahora)
                .map(turno -> new Destinatario(turno.getResponsable(), MotivoDestinatario.GUARDIA, "De guardia en " + rotacion))
                .orElseGet(() -> new Destinatario(null, MotivoDestinatario.NINGUNO, "Hueco de guardia en " + rotacion));
        }
        return new Destinatario(null, MotivoDestinatario.NINGUNO, "El paso " + paso.getOrden() + " no tiene destinatario ni rotacion");
    }

    private Optional<User> autorDelCambioSospechoso(Incidente incidente, Instant ahora) {
        List<CorrelacionCambioIncidente> correlaciones = escalamientoRepository.correlacionesDelIncidente(incidente.getId());
        if (correlaciones.isEmpty()) {
            correlaciones = sugerirCorrelacion(incidente, ahora).map(List::of).orElse(List.of());
        }
        return correlaciones
            .stream()
            .filter(c -> c.getEstado() != EstadoCorrelacion.DESCARTADA)
            .sorted(
                Comparator.<CorrelacionCambioIncidente>comparingInt(c ->
                    c.getEstado() == EstadoCorrelacion.CONFIRMADA ? 0 : 1
                ).thenComparing(c -> c.getDespliegue().getIniciadoEn(), Comparator.reverseOrder())
            )
            .map(c -> c.getDespliegue().getCambio().getAutor())
            .findFirst();
    }

    private Optional<CorrelacionCambioIncidente> sugerirCorrelacion(Incidente incidente, Instant ahora) {
        Instant detectado = incidente.getDetectadoEn();
        Instant desde = detectado.minus(ventanaCorrelacionMinutos, ChronoUnit.MINUTES);
        Optional<DespliegueDeServicio> candidato = escalamientoRepository
            .desplieguesCandidatos(incidente.getId(), desde, detectado, ResultadoDespliegue.REVERTIDO)
            .stream()
            .findFirst();
        return candidato.map(despliegue -> {
            int minutos = (int) Duration.between(despliegue.getIniciadoEn(), detectado).toMinutes();
            CorrelacionCambioIncidente correlacion = new CorrelacionCambioIncidente();
            correlacion.setOrigen(OrigenCorrelacion.AUTOMATICA);
            correlacion.setEstado(EstadoCorrelacion.SUGERIDA);
            correlacion.setDetectadaEn(ahora);
            correlacion.setMinutosEntreDespliegueEIncidente(minutos);
            correlacion.setNota(
                recortar(
                    "Despliegue " + despliegue.getVersion() + " de '" + despliegue.getCambio().getTitulo() + "' " + minutos + " min antes",
                    500
                )
            );
            correlacion.setIncidente(incidente);
            correlacion.setDespliegue(despliegue);
            return correlacionRepository.save(correlacion);
        });
    }

    public Optional<EstadoDeCorteDTO> reconocer(Long incidenteId) {
        Instant ahora = clock.instant();
        return escalamientoRepository.bloquearIncidente(incidenteId).map(incidente -> {
            if (incidente.getEstado() == EstadoIncidente.ABIERTO) {
                incidente.setEstado(EstadoIncidente.RECONOCIDO);
                incidente.setReconocidoEn(ahora);
                evento(incidente, TipoEvento.CAMBIO_DE_ESTADO, "Incidente reconocido: se corta el escalamiento", ahora);
            }
            return corte(incidente);
        });
    }

    public Optional<EstadoDeCorteDTO> silenciar(Long incidenteId, int minutos) {
        Instant ahora = clock.instant();
        return escalamientoRepository.bloquearIncidente(incidenteId).map(incidente -> {
            incidente.setSilenciadoHasta(ahora.plus(minutos, ChronoUnit.MINUTES));
            evento(incidente, TipoEvento.NOTA, "Escalamiento silenciado hasta " + incidente.getSilenciadoHasta(), ahora);
            return corte(incidente);
        });
    }

    @Transactional(readOnly = true)
    public List<EscalonDTO> historial(Long incidenteId) {
        return escalamientoRepository.historial(incidenteId).stream().map(EscalonDTO::de).toList();
    }

    private EjecucionDeEscalamiento notificar(
        Incidente incidente,
        int vuelta,
        int nivel,
        PasoEscalamiento paso,
        User usuario,
        MotivoDestinatario motivo,
        Canal canal,
        Instant ahora,
        Instant proximo,
        String detalle
    ) {
        Notificacion notificacion = new Notificacion();
        notificacion.setCanal(canal);
        notificacion.setDestino(recortar(usuario.getEmail() != null ? usuario.getEmail() : usuario.getLogin(), 120));
        notificacion.setEstado(EstadoNotificacion.PENDIENTE);
        notificacion.setIntentos(0);
        notificacion.setIncidente(incidente);
        notificacion.setDestinatario(usuario);
        notificacionRepository.save(notificacion);
        return registrar(incidente, vuelta, nivel, paso, usuario, motivo, ResultadoEscalamiento.NOTIFICADO, ahora, proximo, detalle);
    }

    private EjecucionDeEscalamiento registrar(
        Incidente incidente,
        int vuelta,
        int nivel,
        PasoEscalamiento paso,
        User destinatario,
        MotivoDestinatario motivo,
        ResultadoEscalamiento resultado,
        Instant ahora,
        Instant proximo,
        String detalle
    ) {
        EjecucionDeEscalamiento escalon = new EjecucionDeEscalamiento();
        escalon.setVuelta(vuelta);
        escalon.setNivel(nivel);
        escalon.setMotivo(motivo);
        escalon.setResultado(resultado);
        escalon.setEjecutadoEn(ahora);
        escalon.setProximoEn(proximo);
        escalon.setDetalle(recortar(detalle, 500));
        escalon.setIncidente(incidente);
        escalon.setPaso(paso);
        escalon.setDestinatario(destinatario);
        EjecucionDeEscalamiento guardado = escalamientoRepository.save(escalon);

        String quien = destinatario != null ? " a " + destinatario.getLogin() : "";
        evento(incidente, TipoEvento.ESCALAMIENTO, resultado + quien + " (vuelta " + vuelta + ", nivel " + nivel + "): " + detalle, ahora);
        return guardado;
    }

    private void evento(Incidente incidente, TipoEvento tipo, String detalle, Instant ahora) {
        EventoDeIncidente evento = new EventoDeIncidente();
        evento.setTipo(tipo);
        evento.setDetalle(recortar(detalle, 500));
        evento.setOcurridoEn(ahora);
        evento.setAutomatico(true);
        evento.setIncidente(incidente);
        eventoDeIncidenteRepository.save(evento);
    }

    private ResultadoEscalamientoDTO resultado(
        Incidente incidente,
        Instant ahora,
        Accion accion,
        String detalle,
        List<EscalonDTO> escalones,
        Instant proximaEvaluacion
    ) {
        return new ResultadoEscalamientoDTO(incidente.getId(), ahora, accion, detalle, escalones, proximaEvaluacion);
    }

    private static EstadoDeCorteDTO corte(Incidente incidente) {
        return new EstadoDeCorteDTO(incidente.getId(), incidente.getEstado(), incidente.getReconocidoEn(), incidente.getSilenciadoHasta());
    }

    private static String recortar(String texto, int maximo) {
        return texto == null || texto.length() <= maximo ? texto : texto.substring(0, maximo);
    }
}
