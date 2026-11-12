package ar.edu.um.isa.oncall.service.criteria;

import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoDespliegue;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ar.edu.um.isa.oncall.domain.DespliegueDeServicio} entity. This class is used
 * in {@link ar.edu.um.isa.oncall.web.rest.DespliegueDeServicioResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /despliegue-de-servicios?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DespliegueDeServicioCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Entorno
     */
    public static class EntornoFilter extends Filter<Entorno> {

        public EntornoFilter() {}

        public EntornoFilter(EntornoFilter filter) {
            super(filter);
        }

        @Override
        public EntornoFilter copy() {
            return new EntornoFilter(this);
        }
    }

    /**
     * Class for filtering ResultadoDespliegue
     */
    public static class ResultadoDespliegueFilter extends Filter<ResultadoDespliegue> {

        public ResultadoDespliegueFilter() {}

        public ResultadoDespliegueFilter(ResultadoDespliegueFilter filter) {
            super(filter);
        }

        @Override
        public ResultadoDespliegueFilter copy() {
            return new ResultadoDespliegueFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter version;

    private EntornoFilter entorno;

    private InstantFilter iniciadoEn;

    private InstantFilter finalizadoEn;

    private ResultadoDespliegueFilter resultado;

    private LongFilter cambioId;

    private LongFilter servicioId;

    private LongFilter ejecutadoPorId;

    private LongFilter correlacionId;

    private Boolean distinct;

    public DespliegueDeServicioCriteria() {}

    public DespliegueDeServicioCriteria(DespliegueDeServicioCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.version = other.optionalVersion().map(StringFilter::copy).orElse(null);
        this.entorno = other.optionalEntorno().map(EntornoFilter::copy).orElse(null);
        this.iniciadoEn = other.optionalIniciadoEn().map(InstantFilter::copy).orElse(null);
        this.finalizadoEn = other.optionalFinalizadoEn().map(InstantFilter::copy).orElse(null);
        this.resultado = other.optionalResultado().map(ResultadoDespliegueFilter::copy).orElse(null);
        this.cambioId = other.optionalCambioId().map(LongFilter::copy).orElse(null);
        this.servicioId = other.optionalServicioId().map(LongFilter::copy).orElse(null);
        this.ejecutadoPorId = other.optionalEjecutadoPorId().map(LongFilter::copy).orElse(null);
        this.correlacionId = other.optionalCorrelacionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DespliegueDeServicioCriteria copy() {
        return new DespliegueDeServicioCriteria(this);
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

    public StringFilter getVersion() {
        return version;
    }

    public Optional<StringFilter> optionalVersion() {
        return Optional.ofNullable(version);
    }

    public StringFilter version() {
        if (version == null) {
            setVersion(new StringFilter());
        }
        return version;
    }

    public void setVersion(StringFilter version) {
        this.version = version;
    }

    public EntornoFilter getEntorno() {
        return entorno;
    }

    public Optional<EntornoFilter> optionalEntorno() {
        return Optional.ofNullable(entorno);
    }

    public EntornoFilter entorno() {
        if (entorno == null) {
            setEntorno(new EntornoFilter());
        }
        return entorno;
    }

    public void setEntorno(EntornoFilter entorno) {
        this.entorno = entorno;
    }

    public InstantFilter getIniciadoEn() {
        return iniciadoEn;
    }

    public Optional<InstantFilter> optionalIniciadoEn() {
        return Optional.ofNullable(iniciadoEn);
    }

    public InstantFilter iniciadoEn() {
        if (iniciadoEn == null) {
            setIniciadoEn(new InstantFilter());
        }
        return iniciadoEn;
    }

    public void setIniciadoEn(InstantFilter iniciadoEn) {
        this.iniciadoEn = iniciadoEn;
    }

    public InstantFilter getFinalizadoEn() {
        return finalizadoEn;
    }

    public Optional<InstantFilter> optionalFinalizadoEn() {
        return Optional.ofNullable(finalizadoEn);
    }

    public InstantFilter finalizadoEn() {
        if (finalizadoEn == null) {
            setFinalizadoEn(new InstantFilter());
        }
        return finalizadoEn;
    }

    public void setFinalizadoEn(InstantFilter finalizadoEn) {
        this.finalizadoEn = finalizadoEn;
    }

    public ResultadoDespliegueFilter getResultado() {
        return resultado;
    }

    public Optional<ResultadoDespliegueFilter> optionalResultado() {
        return Optional.ofNullable(resultado);
    }

    public ResultadoDespliegueFilter resultado() {
        if (resultado == null) {
            setResultado(new ResultadoDespliegueFilter());
        }
        return resultado;
    }

    public void setResultado(ResultadoDespliegueFilter resultado) {
        this.resultado = resultado;
    }

    public LongFilter getCambioId() {
        return cambioId;
    }

    public Optional<LongFilter> optionalCambioId() {
        return Optional.ofNullable(cambioId);
    }

    public LongFilter cambioId() {
        if (cambioId == null) {
            setCambioId(new LongFilter());
        }
        return cambioId;
    }

    public void setCambioId(LongFilter cambioId) {
        this.cambioId = cambioId;
    }

    public LongFilter getServicioId() {
        return servicioId;
    }

    public Optional<LongFilter> optionalServicioId() {
        return Optional.ofNullable(servicioId);
    }

    public LongFilter servicioId() {
        if (servicioId == null) {
            setServicioId(new LongFilter());
        }
        return servicioId;
    }

    public void setServicioId(LongFilter servicioId) {
        this.servicioId = servicioId;
    }

    public LongFilter getEjecutadoPorId() {
        return ejecutadoPorId;
    }

    public Optional<LongFilter> optionalEjecutadoPorId() {
        return Optional.ofNullable(ejecutadoPorId);
    }

    public LongFilter ejecutadoPorId() {
        if (ejecutadoPorId == null) {
            setEjecutadoPorId(new LongFilter());
        }
        return ejecutadoPorId;
    }

    public void setEjecutadoPorId(LongFilter ejecutadoPorId) {
        this.ejecutadoPorId = ejecutadoPorId;
    }

    public LongFilter getCorrelacionId() {
        return correlacionId;
    }

    public Optional<LongFilter> optionalCorrelacionId() {
        return Optional.ofNullable(correlacionId);
    }

    public LongFilter correlacionId() {
        if (correlacionId == null) {
            setCorrelacionId(new LongFilter());
        }
        return correlacionId;
    }

    public void setCorrelacionId(LongFilter correlacionId) {
        this.correlacionId = correlacionId;
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
        final DespliegueDeServicioCriteria that = (DespliegueDeServicioCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(version, that.version) &&
            Objects.equals(entorno, that.entorno) &&
            Objects.equals(iniciadoEn, that.iniciadoEn) &&
            Objects.equals(finalizadoEn, that.finalizadoEn) &&
            Objects.equals(resultado, that.resultado) &&
            Objects.equals(cambioId, that.cambioId) &&
            Objects.equals(servicioId, that.servicioId) &&
            Objects.equals(ejecutadoPorId, that.ejecutadoPorId) &&
            Objects.equals(correlacionId, that.correlacionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            version,
            entorno,
            iniciadoEn,
            finalizadoEn,
            resultado,
            cambioId,
            servicioId,
            ejecutadoPorId,
            correlacionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DespliegueDeServicioCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalVersion().map(f -> "version=" + f + ", ").orElse("") +
            optionalEntorno().map(f -> "entorno=" + f + ", ").orElse("") +
            optionalIniciadoEn().map(f -> "iniciadoEn=" + f + ", ").orElse("") +
            optionalFinalizadoEn().map(f -> "finalizadoEn=" + f + ", ").orElse("") +
            optionalResultado().map(f -> "resultado=" + f + ", ").orElse("") +
            optionalCambioId().map(f -> "cambioId=" + f + ", ").orElse("") +
            optionalServicioId().map(f -> "servicioId=" + f + ", ").orElse("") +
            optionalEjecutadoPorId().map(f -> "ejecutadoPorId=" + f + ", ").orElse("") +
            optionalCorrelacionId().map(f -> "correlacionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
