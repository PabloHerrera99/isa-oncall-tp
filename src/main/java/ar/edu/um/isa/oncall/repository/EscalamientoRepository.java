package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.CorrelacionCambioIncidente;
import ar.edu.um.isa.oncall.domain.DespliegueDeServicio;
import ar.edu.um.isa.oncall.domain.EjecucionDeEscalamiento;
import ar.edu.um.isa.oncall.domain.Incidente;
import ar.edu.um.isa.oncall.domain.PasoEscalamiento;
import ar.edu.um.isa.oncall.domain.PoliticaEscalamiento;
import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoDespliegue;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Todas las consultas que necesita la cadena de escalamiento
 * Separado de los repositorios generados para que jhipster no lo
 * pise.
 */
@Repository
public interface EscalamientoRepository extends JpaRepository<EjecucionDeEscalamiento, Long> {
    @Query("select i.id from Incidente i where i.estado = :estado order by i.detectadoEn asc, i.id asc")
    List<Long> idsDeIncidentesEnEstado(@Param("estado") EstadoIncidente estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Incidente i where i.id = :id")
    Optional<Incidente> bloquearIncidente(@Param("id") Long id);

    /**
     * El ultimo escalon ejecutado sobre el incidente: la memoria de la cadena.
     */
    Optional<EjecucionDeEscalamiento> findFirstByIncidenteIdOrderByIdDesc(Long incidenteId);

    /**
     * Todos los escalones de un incidente, en el orden en que se ejecutaron.
     */
    @Query(
        """
        select e from EjecucionDeEscalamiento e
        left join fetch e.destinatario
        left join fetch e.paso
        where e.incidente.id = :incidenteId
        order by e.id asc
        """
    )
    List<EjecucionDeEscalamiento> historial(@Param("incidenteId") Long incidenteId);

    /**
     * Politicas de los servicios afectados por el incidente, el servicio mas
     * critico primero (TIER1 antes que TIER2 antes que TIER3).
     */
    @Query(
        """
        select p from PoliticaEscalamiento p
        join fetch p.servicio s
        where s.id in (select s2.id from Incidente i join i.servicios s2 where i.id = :incidenteId)
        order by s.criticidad asc, p.id asc
        """
    )
    List<PoliticaEscalamiento> politicasDelIncidente(@Param("incidenteId") Long incidenteId);

    /**
     * Pasos de una politica, con lo necesario para saber a quien avisar.
     */
    @Query(
        """
        select p from PasoEscalamiento p
        left join fetch p.rotacion
        left join fetch p.destinatarioDirecto
        where p.politica.id = :politicaId
        order by p.orden asc, p.id asc
        """
    )
    List<PasoEscalamiento> pasosDePolitica(@Param("politicaId") Long politicaId);

    /**
     * Correlaciones del incidente (todas, incluso descartadas), con el camino
     * hasta el autor del cambio ya cargado.
     */
    @Query(
        """
        select c from CorrelacionCambioIncidente c
        join fetch c.despliegue d
        join fetch d.cambio ca
        join fetch ca.autor
        where c.incidente.id = :incidenteId
        """
    )
    List<CorrelacionCambioIncidente> correlacionesDelIncidente(@Param("incidenteId") Long incidenteId);

    /**
     * Despliegues sobre los servicios del incidente que empezaron dentro de la
     * ventana [desde, hasta] y no fueron revertidos. El mas reciente primero.
     */
    @Query(
        """
        select d from DespliegueDeServicio d
        join fetch d.cambio ca
        join fetch ca.autor
        where d.servicio.id in (select s.id from Incidente i join i.servicios s where i.id = :incidenteId)
          and d.iniciadoEn >= :desde
          and d.iniciadoEn <= :hasta
          and d.resultado <> :excluido
        order by d.iniciadoEn desc, d.id desc
        """
    )
    List<DespliegueDeServicio> desplieguesCandidatos(
        @Param("incidenteId") Long incidenteId,
        @Param("desde") Instant desde,
        @Param("hasta") Instant hasta,
        @Param("excluido") ResultadoDespliegue excluido
    );
}
