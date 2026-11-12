package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.*; // for static metamodels
import ar.edu.um.isa.oncall.domain.Cambio;
import ar.edu.um.isa.oncall.repository.CambioRepository;
import ar.edu.um.isa.oncall.service.criteria.CambioCriteria;
import ar.edu.um.isa.oncall.service.dto.CambioDTO;
import ar.edu.um.isa.oncall.service.mapper.CambioMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Cambio} entities in the database.
 * The main input is a {@link CambioCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CambioDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CambioQueryService extends QueryService<Cambio> {

    private static final Logger LOG = LoggerFactory.getLogger(CambioQueryService.class);

    private final CambioRepository cambioRepository;

    private final CambioMapper cambioMapper;

    public CambioQueryService(CambioRepository cambioRepository, CambioMapper cambioMapper) {
        this.cambioRepository = cambioRepository;
        this.cambioMapper = cambioMapper;
    }

    /**
     * Return a {@link Page} of {@link CambioDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CambioDTO> findByCriteria(CambioCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Cambio> specification = createSpecification(criteria);
        return cambioRepository.findAll(specification, page).map(cambioMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CambioCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Cambio> specification = createSpecification(criteria);
        return cambioRepository.count(specification);
    }

    /**
     * Function to convert {@link CambioCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Cambio> createSpecification(CambioCriteria criteria) {
        Specification<Cambio> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Cambio_.autor, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Cambio_.id),
                    buildStringSpecification(criteria.getTitulo(), Cambio_.titulo),
                    buildStringSpecification(criteria.getDescripcion(), Cambio_.descripcion),
                    buildSpecification(criteria.getTipo(), Cambio_.tipo),
                    buildSpecification(criteria.getRiesgo(), Cambio_.riesgo),
                    buildStringSpecification(criteria.getTicketUrl(), Cambio_.ticketUrl),
                    buildRangeSpecification(criteria.getCreadoEn(), Cambio_.creadoEn),
                    buildSpecification(criteria.getAutorId(), root -> root.join(Cambio_.autor, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getDespliegueId(), root ->
                        root.join(Cambio_.despliegues, JoinType.LEFT).get(DespliegueDeServicio_.id)
                    )
                )
            );
        }
        return specification;
    }
}
