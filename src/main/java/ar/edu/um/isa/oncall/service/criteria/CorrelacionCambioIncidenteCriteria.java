package ar.edu.um.isa.oncall.service.criteria;

import ar.edu.um.isa.oncall.domain.enumeration.EstadoCorrelacion;
import ar.edu.um.isa.oncall.domain.enumeration.OrigenCorrelacion;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ar.edu.um.isa.oncall.domain.CorrelacionCambioIncidente} entity. This class is used
 * in {@link ar.edu.um.isa.oncall.web.rest.CorrelacionCambioIncidenteResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /correlacion-cambio-incidentes?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CorrelacionCambioIncidenteCriteria implements Serializable, Criteria {

    /**
     * Class for filtering OrigenCorrelacion
     */
    public static class OrigenCorrelacionFilter extends Filter<OrigenCorrelacion> {

        public OrigenCorrelacionFilter() {}

        public OrigenCorrelacionFilter(OrigenCorrelacionFilter filter) {
            super(filter);
        }

        @Override
        public OrigenCorrelacionFilter copy() {
            return new OrigenCorrelacionFilter(this);
        }
    }

    /**
     * Class for filtering EstadoCorrelacion
     */
    public static class EstadoCorrelacionFilter extends Filter<EstadoCorrelacion> {

        public EstadoCorrelacionFilter() {}

        public EstadoCorrelacionFilter(EstadoCorrelacionFilter filter) {
            super(filter);
        }

        @Override
        public EstadoCorrelacionFilter copy() {
            return new EstadoCorrelacionFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private OrigenCorrelacionFilter origen;

    private EstadoCorrelacionFilter estado;

    private InstantFilter detectadaEn;

    private IntegerFilter minutosEntreDespliegueEIncidente;

    private StringFilter nota;

    private LongFilter incidenteId;

    private LongFilter despliegueId;

    private LongFilter confirmadaPorId;

    private Boolean distinct;

    public CorrelacionCambioIncidenteCriteria() {}

    public CorrelacionCambioIncidenteCriteria(CorrelacionCambioIncidenteCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.origen = other.optionalOrigen().map(OrigenCorrelacionFilter::copy).orElse(null);
        this.estado = other.optionalEstado().map(EstadoCorrelacionFilter::copy).orElse(null);
        this.detectadaEn = other.optionalDetectadaEn().map(InstantFilter::copy).orElse(null);
        this.minutosEntreDespliegueEIncidente = other.optionalMinutosEntreDespliegueEIncidente().map(IntegerFilter::copy).orElse(null);
        this.nota = other.optionalNota().map(StringFilter::copy).orElse(null);
        this.incidenteId = other.optionalIncidenteId().map(LongFilter::copy).orElse(null);
        this.despliegueId = other.optionalDespliegueId().map(LongFilter::copy).orElse(null);
        this.confirmadaPorId = other.optionalConfirmadaPorId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CorrelacionCambioIncidenteCriteria copy() {
        return new CorrelacionCambioIncidenteCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public OrigenCorrelacionFilter getOrigen() {
        return origen;
    }

    public Optional<OrigenCorrelacionFilter> optionalOrigen() {
        return Optional.ofNullable(origen);
    }

    public OrigenCorrelacionFilter origen() {
        if (origen == null) {
            setOrigen(new OrigenCorrelacionFilter());
        }
        return origen;
    }

    public void setOrigen(OrigenCorrelacionFilter origen) {
        this.origen = origen;
    }

    public EstadoCorrelacionFilter getEstado() {
        return estado;
    }

    public Optional<EstadoCorrelacionFilter> optionalEstado() {
        return Optional.ofNullable(estado);
    }

    public EstadoCorrelacionFilter estado() {
        if (estado == null) {
            setEstado(new EstadoCorrelacionFilter());
        }
        return estado;
    }

    public void setEstado(EstadoCorrelacionFilter estado) {
        this.estado = estado;
    }

    public InstantFilter getDetectadaEn() {
        return detectadaEn;
    }

    public Optional<InstantFilter> optionalDetectadaEn() {
        return Optional.ofNullable(detectadaEn);
    }

    public InstantFilter detectadaEn() {
        if (detectadaEn == null) {
            setDetectadaEn(new InstantFilter());
        }
        return detectadaEn;
    }

    public void setDetectadaEn(InstantFilter detectadaEn) {
        this.detectadaEn = detectadaEn;
    }

    public IntegerFilter getMinutosEntreDespliegueEIncidente() {
        return minutosEntreDespliegueEIncidente;
    }

    public Optional<IntegerFilter> optionalMinutosEntreDespliegueEIncidente() {
        return Optional.ofNullable(minutosEntreDespliegueEIncidente);
    }

    public IntegerFilter minutosEntreDespliegueEIncidente() {
        if (minutosEntreDespliegueEIncidente == null) {
            setMinutosEntreDespliegueEIncidente(new IntegerFilter());
        }
        return minutosEntreDespliegueEIncidente;
    }

    public void setMinutosEntreDespliegueEIncidente(IntegerFilter minutosEntreDespliegueEIncidente) {
        this.minutosEntreDespliegueEIncidente = minutosEntreDespliegueEIncidente;
    }

    public StringFilter getNota() {
        return nota;
    }

    public Optional<StringFilter> optionalNota() {
        return Optional.ofNullable(nota);
    }

    public StringFilter nota() {
        if (nota == null) {
            setNota(new StringFilter());
        }
        return nota;
    }

    public void setNota(StringFilter nota) {
        this.nota = nota;
    }

    public LongFilter getIncidenteId() {
        return incidenteId;
    }

    public Optional<LongFilter> optionalIncidenteId() {
        return Optional.ofNullable(incidenteId);
    }

    public LongFilter incidenteId() {
        if (incidenteId == null) {
            setIncidenteId(new LongFilter());
        }
        return incidenteId;
    }

    public void setIncidenteId(LongFilter incidenteId) {
        this.incidenteId = incidenteId;
    }

    public LongFilter getDespliegueId() {
        return despliegueId;
    }

    public Optional<LongFilter> optionalDespliegueId() {
        return Optional.ofNullable(despliegueId);
    }

    public LongFilter despliegueId() {
        if (despliegueId == null) {
            setDespliegueId(new LongFilter());
        }
        return despliegueId;
    }

    public void setDespliegueId(LongFilter despliegueId) {
        this.despliegueId = despliegueId;
    }

    public LongFilter getConfirmadaPorId() {
        return confirmadaPorId;
    }

    public Optional<LongFilter> optionalConfirmadaPorId() {
        return Optional.ofNullable(confirmadaPorId);
    }

    public LongFilter confirmadaPorId() {
        if (confirmadaPorId == null) {
            setConfirmadaPorId(new LongFilter());
        }
        return confirmadaPorId;
    }

    public void setConfirmadaPorId(LongFilter confirmadaPorId) {
        this.confirmadaPorId = confirmadaPorId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CorrelacionCambioIncidenteCriteria that = (CorrelacionCambioIncidenteCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(origen, that.origen) &&
            Objects.equals(estado, that.estado) &&
            Objects.equals(detectadaEn, that.detectadaEn) &&
            Objects.equals(minutosEntreDespliegueEIncidente, that.minutosEntreDespliegueEIncidente) &&
            Objects.equals(nota, that.nota) &&
            Objects.equals(incidenteId, that.incidenteId) &&
            Objects.equals(despliegueId, that.despliegueId) &&
            Objects.equals(confirmadaPorId, that.confirmadaPorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            origen,
            estado,
            detectadaEn,
            minutosEntreDespliegueEIncidente,
            nota,
            incidenteId,
            despliegueId,
            confirmadaPorId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CorrelacionCambioIncidenteCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalOrigen().map(f -> "origen=" + f + ", ").orElse("") +
            optionalEstado().map(f -> "estado=" + f + ", ").orElse("") +
            optionalDetectadaEn().map(f -> "detectadaEn=" + f + ", ").orElse("") +
            optionalMinutosEntreDespliegueEIncidente().map(f -> "minutosEntreDespliegueEIncidente=" + f + ", ").orElse("") +
            optionalNota().map(f -> "nota=" + f + ", ").orElse("") +
            optionalIncidenteId().map(f -> "incidenteId=" + f + ", ").orElse("") +
            optionalDespliegueId().map(f -> "despliegueId=" + f + ", ").orElse("") +
            optionalConfirmadaPorId().map(f -> "confirmadaPorId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
