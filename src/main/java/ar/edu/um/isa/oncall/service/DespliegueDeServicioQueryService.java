package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.*; // for static metamodels
import ar.edu.um.isa.oncall.domain.DespliegueDeServicio;
import ar.edu.um.isa.oncall.repository.DespliegueDeServicioRepository;
import ar.edu.um.isa.oncall.service.criteria.DespliegueDeServicioCriteria;
import ar.edu.um.isa.oncall.service.dto.DespliegueDeServicioDTO;
import ar.edu.um.isa.oncall.service.mapper.DespliegueDeServicioMapper;
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
 * Service for executing complex queries for {@link DespliegueDeServicio} entities in the database.
 * The main input is a {@link DespliegueDeServicioCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DespliegueDeServicioDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DespliegueDeServicioQueryService extends QueryService<DespliegueDeServicio> {

    private static final Logger LOG = LoggerFactory.getLogger(DespliegueDeServicioQueryService.class);

    private final DespliegueDeServicioRepository despliegueDeServicioRepository;

    private final DespliegueDeServicioMapper despliegueDeServicioMapper;

    public DespliegueDeServicioQueryService(
        DespliegueDeServicioRepository despliegueDeServicioRepository,
        DespliegueDeServicioMapper despliegueDeServicioMapper
    ) {
        this.despliegueDeServicioRepository = despliegueDeServicioRepository;
        this.despliegueDeServicioMapper = despliegueDeServicioMapper;
    }

    /**
     * Return a {@link Page} of {@link DespliegueDeServicioDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DespliegueDeServicioDTO> findByCriteria(DespliegueDeServicioCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DespliegueDeServicio> specification = createSpecification(criteria);
        return despliegueDeServicioRepository.findAll(specification, page).map(despliegueDeServicioMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DespliegueDeServicioCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DespliegueDeServicio> specification = createSpecification(criteria);
        return despliegueDeServicioRepository.count(specification);
    }

    /**
     * Function to convert {@link DespliegueDeServicioCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DespliegueDeServicio> createSpecification(DespliegueDeServicioCriteria criteria) {
        Specification<DespliegueDeServicio> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(DespliegueDeServicio_.cambio, JoinType.LEFT);
                root.fetch(DespliegueDeServicio_.servicio, JoinType.LEFT);
                root.fetch(DespliegueDeServicio_.ejecutadoPor, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), DespliegueDeServicio_.id),
                    buildStringSpecification(criteria.getVersion(), DespliegueDeServicio_.version),
                    buildSpecification(criteria.getEntorno(), DespliegueDeServicio_.entorno),
                    buildRangeSpecification(criteria.getIniciadoEn(), DespliegueDeServicio_.iniciadoEn),
                    buildRangeSpecification(criteria.getFinalizadoEn(), DespliegueDeServicio_.finalizadoEn),
                    buildSpecification(criteria.getResultado(), DespliegueDeServicio_.resultado),
                    buildSpecification(criteria.getCambioId(), root ->
                        root.join(DespliegueDeServicio_.cambio, JoinType.LEFT).get(Cambio_.id)
                    ),
                    buildSpecification(criteria.getServicioId(), root ->
                        root.join(DespliegueDeServicio_.servicio, JoinType.LEFT).get(Servicio_.id)
                    ),
                    buildSpecification(criteria.getEjecutadoPorId(), root ->
                        root.join(DespliegueDeServicio_.ejecutadoPor, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getCorrelacionId(), root ->
                        root.join(DespliegueDeServicio_.correlacions, JoinType.LEFT).get(CorrelacionCambioIncidente_.id)
                    )
                )
            );
        }
        return specification;
    }
}
