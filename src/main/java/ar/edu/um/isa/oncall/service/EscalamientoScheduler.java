package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.service.dto.ResultadoEscalamientoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EscalamientoScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(EscalamientoScheduler.class);

    private final EscalamientoService escalamientoService;
    private final boolean activo;

    public EscalamientoScheduler(
        EscalamientoService escalamientoService,
        @Value("${oncall.escalamiento.scheduler-activo:true}") boolean activo
    ) {
        this.escalamientoService = escalamientoService;
        this.activo = activo;
    }

    @Scheduled(
        initialDelayString = "${oncall.escalamiento.retardo-inicial-ms:30000}",
        fixedDelayString = "${oncall.escalamiento.intervalo-ms:60000}"
    )
    public void despertar() {
        if (!activo) {
            return;
        }
        for (Long incidenteId : escalamientoService.incidentesAEvaluar()) {
            try {
                escalamientoService
                    .evaluar(incidenteId)
                    .filter(r -> r.accion() == ResultadoEscalamientoDTO.Accion.ESCALADO)
                    .ifPresent(r -> LOG.info("Incidente {} escalado: {}", incidenteId, r.detalle()));
            } catch (RuntimeException e) {
                // Un incidente con datos rotos no tiene que frenar a los demas.
                LOG.warn("No se pudo evaluar el escalamiento del incidente {}: {}", incidenteId, e.getMessage());
            }
        }
    }
}
