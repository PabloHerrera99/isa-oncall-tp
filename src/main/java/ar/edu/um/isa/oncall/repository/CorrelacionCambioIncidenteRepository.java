package ar.edu.um.isa.oncall.repository;

import ar.edu.um.isa.oncall.domain.CorrelacionCambioIncidente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CorrelacionCambioIncidente entity.
 */
@Repository
public interface CorrelacionCambioIncidenteRepository
    extends JpaRepository<CorrelacionCambioIncidente, Long>, JpaSpecificationExecutor<CorrelacionCambioIncidente>
{
    @Query(
        "select correlacionCambioIncidente from CorrelacionCambioIncidente correlacionCambioIncidente where correlacionCambioIncidente.confirmadaPor.login = ?#{authentication.name}"
    )
    List<CorrelacionCambioIncidente> findByConfirmadaPorIsCurrentUser();

    default Optional<CorrelacionCambioIncidente> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CorrelacionCambioIncidente> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CorrelacionCambioIncidente> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select correlacionCambioIncidente from CorrelacionCambioIncidente correlacionCambioIncidente left join fetch correlacionCambioIncidente.incidente left join fetch correlacionCambioIncidente.despliegue left join fetch correlacionCambioIncidente.confirmadaPor",
        countQuery = "select count(correlacionCambioIncidente) from CorrelacionCambioIncidente correlacionCambioIncidente"
    )
    Page<CorrelacionCambioIncidente> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select correlacionCambioIncidente from CorrelacionCambioIncidente correlacionCambioIncidente left join fetch correlacionCambioIncidente.incidente left join fetch correlacionCambioIncidente.despliegue left join fetch correlacionCambioIncidente.confirmadaPor"
    )
    List<CorrelacionCambioIncidente> findAllWithToOneRelationships();

    @Query(
        "select correlacionCambioIncidente from CorrelacionCambioIncidente correlacionCambioIncidente left join fetch correlacionCambioIncidente.incidente left join fetch correlacionCambioIncidente.despliegue left join fetch correlacionCambioIncidente.confirmadaPor where correlacionCambioIncidente.id =:id"
    )
    Optional<CorrelacionCambioIncidente> findOneWithToOneRelationships(@Param("id") Long id);
}
