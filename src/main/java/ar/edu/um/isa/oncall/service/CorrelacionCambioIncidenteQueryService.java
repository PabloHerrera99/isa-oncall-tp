package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.*; // for static metamodels
import ar.edu.um.isa.oncall.domain.CorrelacionCambioIncidente;
import ar.edu.um.isa.oncall.repository.CorrelacionCambioIncidenteRepository;
import ar.edu.um.isa.oncall.service.criteria.CorrelacionCambioIncidenteCriteria;
import ar.edu.um.isa.oncall.service.dto.CorrelacionCambioIncidenteDTO;
import ar.edu.um.isa.oncall.service.mapper.CorrelacionCambioIncidenteMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link CorrelacionCambioIncidente} entities in the database.
 * The main input is a {@link CorrelacionCambioIncidenteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CorrelacionCambioIncidenteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CorrelacionCambioIncidenteQueryService extends QueryService<CorrelacionCambioIncidente> {

    private static final Logger LOG = LoggerFactory.getLogger(CorrelacionCambioIncidenteQueryService.class);

    private final CorrelacionCambioIncidenteRepository correlacionCambioIncidenteRepository;

    private final CorrelacionCambioIncidenteMapper correlacionCambioIncidenteMapper;

    public CorrelacionCambioIncidenteQueryService(
        CorrelacionCambioIncidenteRepository correlacionCambioIncidenteRepository,
        CorrelacionCambioIncidenteMapper correlacionCambioIncidenteMapper
    ) {
        this.correlacionCambioIncidenteRepository = correlacionCambioIncidenteRepository;
        this.correlacionCambioIncidenteMapper = correlacionCambioIncidenteMapper;
    }

    /**
     * Return a {@link List} of {@link CorrelacionCambioIncidenteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CorrelacionCambioIncidenteDTO> findByCriteria(CorrelacionCambioIncidenteCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<CorrelacionCambioIncidente> specification = createSpecification(criteria);
        return correlacionCambioIncidenteMapper.toDto(correlacionCambioIncidenteRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CorrelacionCambioIncidenteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CorrelacionCambioIncidente> specification = createSpecification(criteria);
        return correlacionCambioIncidenteRepository.count(specification);
    }

    /**
     * Function to convert {@link CorrelacionCambioIncidenteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CorrelacionCambioIncidente> createSpecification(CorrelacionCambioIncidenteCriteria criteria) {
        Specification<CorrelacionCambioIncidente> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CorrelacionCambioIncidente_.incidente, JoinType.LEFT);
                root.fetch(CorrelacionCambioIncidente_.despliegue, JoinType.LEFT);
                root.fetch(CorrelacionCambioIncidente_.confirmadaPor, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CorrelacionCambioIncidente_.id),
                    buildSpecification(criteria.getOrigen(), CorrelacionCambioIncidente_.origen),
                    buildSpecification(criteria.getEstado(), CorrelacionCambioIncidente_.estado),
                    buildRangeSpecification(criteria.getDetectadaEn(), CorrelacionCambioIncidente_.detectadaEn),
                    buildRangeSpecification(
                        criteria.getMinutosEntreDespliegueEIncidente(),
                        CorrelacionCambioIncidente_.minutosEntreDespliegueEIncidente
                    ),
                    buildStringSpecification(criteria.getNota(), CorrelacionCambioIncidente_.nota),
                    buildSpecification(criteria.getIncidenteId(), root ->
                        root.join(CorrelacionCambioIncidente_.incidente, JoinType.LEFT).get(Incidente_.id)
                    ),
                    buildSpecification(criteria.getDespliegueId(), root ->
                        root.join(CorrelacionCambioIncidente_.despliegue, JoinType.LEFT).get(DespliegueDeServicio_.id)
                    ),
                    buildSpecification(criteria.getConfirmadaPorId(), root ->
                        root.join(CorrelacionCambioIncidente_.confirmadaPor, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
