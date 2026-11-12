package ar.edu.um.isa.oncall.service.criteria;

import ar.edu.um.isa.oncall.domain.enumeration.NivelRiesgo;
import ar.edu.um.isa.oncall.domain.enumeration.TipoCambio;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ar.edu.um.isa.oncall.domain.Cambio} entity. This class is used
 * in {@link ar.edu.um.isa.oncall.web.rest.CambioResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cambios?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CambioCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TipoCambio
     */
    public static class TipoCambioFilter extends Filter<TipoCambio> {

        public TipoCambioFilter() {}

        public TipoCambioFilter(TipoCambioFilter filter) {
            super(filter);
        }

        @Override
        public TipoCambioFilter copy() {
            return new TipoCambioFilter(this);
        }
    }

    /**
     * Class for filtering NivelRiesgo
     */
    public static class NivelRiesgoFilter extends Filter<NivelRiesgo> {

        public NivelRiesgoFilter() {}

        public NivelRiesgoFilter(NivelRiesgoFilter filter) {
            super(filter);
        }

        @Override
        public NivelRiesgoFilter copy() {
            return new NivelRiesgoFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titulo;

    private StringFilter descripcion;

    private TipoCambioFilter tipo;

    private NivelRiesgoFilter riesgo;

    private StringFilter ticketUrl;

    private InstantFilter creadoEn;

    private LongFilter autorId;

    private LongFilter despliegueId;

    private Boolean distinct;

    public CambioCriteria() {}

    public CambioCriteria(CambioCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titulo = other.optionalTitulo().map(StringFilter::copy).orElse(null);
        this.descripcion = other.optionalDescripcion().map(StringFilter::copy).orElse(null);
        this.tipo = other.optionalTipo().map(TipoCambioFilter::copy).orElse(null);
        this.riesgo = other.optionalRiesgo().map(NivelRiesgoFilter::copy).orElse(null);
        this.ticketUrl = other.optionalTicketUrl().map(StringFilter::copy).orElse(null);
        this.creadoEn = other.optionalCreadoEn().map(InstantFilter::copy).orElse(null);
        this.autorId = other.optionalAutorId().map(LongFilter::copy).orElse(null);
        this.despliegueId = other.optionalDespliegueId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CambioCriteria copy() {
        return new CambioCriteria(this);
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

    public StringFilter getTitulo() {
        return titulo;
    }

    public Optional<StringFilter> optionalTitulo() {
        return Optional.ofNullable(titulo);
    }

    public StringFilter titulo() {
        if (titulo == null) {
            setTitulo(new StringFilter());
        }
        return titulo;
    }

    public void setTitulo(StringFilter titulo) {
        this.titulo = titulo;
    }

    public StringFilter getDescripcion() {
        return descripcion;
    }

    public Optional<StringFilter> optionalDescripcion() {
        return Optional.ofNullable(descripcion);
    }

    public StringFilter descripcion() {
        if (descripcion == null) {
            setDescripcion(new StringFilter());
        }
        return descripcion;
    }

    public void setDescripcion(StringFilter descripcion) {
        this.descripcion = descripcion;
    }

    public TipoCambioFilter getTipo() {
        return tipo;
    }

    public Optional<TipoCambioFilter> optionalTipo() {
        return Optional.ofNullable(tipo);
    }

    public TipoCambioFilter tipo() {
        if (tipo == null) {
            setTipo(new TipoCambioFilter());
        }
        return tipo;
    }

    public void setTipo(TipoCambioFilter tipo) {
        this.tipo = tipo;
    }

    public NivelRiesgoFilter getRiesgo() {
        return riesgo;
    }

    public Optional<NivelRiesgoFilter> optionalRiesgo() {
        return Optional.ofNullable(riesgo);
    }

    public NivelRiesgoFilter riesgo() {
        if (riesgo == null) {
            setRiesgo(new NivelRiesgoFilter());
        }
        return riesgo;
    }

    public void setRiesgo(NivelRiesgoFilter riesgo) {
        this.riesgo = riesgo;
    }

    public StringFilter getTicketUrl() {
        return ticketUrl;
    }

    public Optional<StringFilter> optionalTicketUrl() {
        return Optional.ofNullable(ticketUrl);
    }

    public StringFilter ticketUrl() {
        if (ticketUrl == null) {
            setTicketUrl(new StringFilter());
        }
        return ticketUrl;
    }

    public void setTicketUrl(StringFilter ticketUrl) {
        this.ticketUrl = ticketUrl;
    }

    public InstantFilter getCreadoEn() {
        return creadoEn;
    }

    public Optional<InstantFilter> optionalCreadoEn() {
        return Optional.ofNullable(creadoEn);
    }

    public InstantFilter creadoEn() {
        if (creadoEn == null) {
            setCreadoEn(new InstantFilter());
        }
        return creadoEn;
    }

    public void setCreadoEn(InstantFilter creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LongFilter getAutorId() {
        return autorId;
    }

    public Optional<LongFilter> optionalAutorId() {
        return Optional.ofNullable(autorId);
    }

    public LongFilter autorId() {
        if (autorId == null) {
            setAutorId(new LongFilter());
        }
        return autorId;
    }

    public void setAutorId(LongFilter autorId) {
        this.autorId = autorId;
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
        final CambioCriteria that = (CambioCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titulo, that.titulo) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(tipo, that.tipo) &&
            Objects.equals(riesgo, that.riesgo) &&
            Objects.equals(ticketUrl, that.ticketUrl) &&
            Objects.equals(creadoEn, that.creadoEn) &&
            Objects.equals(autorId, that.autorId) &&
            Objects.equals(despliegueId, that.despliegueId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, descripcion, tipo, riesgo, ticketUrl, creadoEn, autorId, despliegueId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CambioCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitulo().map(f -> "titulo=" + f + ", ").orElse("") +
            optionalDescripcion().map(f -> "descripcion=" + f + ", ").orElse("") +
            optionalTipo().map(f -> "tipo=" + f + ", ").orElse("") +
            optionalRiesgo().map(f -> "riesgo=" + f + ", ").orElse("") +
            optionalTicketUrl().map(f -> "ticketUrl=" + f + ", ").orElse("") +
            optionalCreadoEn().map(f -> "creadoEn=" + f + ", ").orElse("") +
            optionalAutorId().map(f -> "autorId=" + f + ", ").orElse("") +
            optionalDespliegueId().map(f -> "despliegueId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
