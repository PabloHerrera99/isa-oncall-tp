package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Consultas de la regla "quien esta de guardia".
 */
@Repository
public interface GuardiaVigenteRepository extends JpaRepository<TurnoDeGuardia, Long> {
    @Query(
        """
        select t from TurnoDeGuardia t
        join fetch t.responsable
        where t.rotacion.id = :rotacionId
          and t.rotacion.activa = true
          and t.desde <= :instante
          and t.hasta > :instante
        order by case when t.esReemplazo = true then 0 else 1 end,
                 t.desde desc,
                 t.id desc
        """
    )
    List<TurnoDeGuardia> findTurnosVigentes(@Param("rotacionId") Long rotacionId, @Param("instante") Instant instante);
}
