package ar.edu.um.isa.oncall.domain;

import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoDespliegue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * La llegada concreta de un cambio a un servicio. Lo que se correlaciona
 * con un incidente es el despliegue, no el cambio en abstracto.
 */
@Entity
@Table(name = "despliegue_de_servicio")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DespliegueDeServicio implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 60)
    @Column(name = "version", length = 60, nullable = false)
    private String version;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "entorno", nullable = false)
    private Entorno entorno;

    @NotNull
    @Column(name = "iniciado_en", nullable = false)
    private Instant iniciadoEn;

    @Column(name = "finalizado_en")
    private Instant finalizadoEn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false)
    private ResultadoDespliegue resultado;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "autor", "despliegues" }, allowSetters = true)
    private Cambio cambio;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "equipo", "objetivos", "alertas", "politicas", "despliegues", "incidentes" }, allowSetters = true)
    private Servicio servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    private User ejecutadoPor;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "despliegue")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "incidente", "despliegue", "confirmadaPor" }, allowSetters = true)
    private Set<CorrelacionCambioIncidente> correlacions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DespliegueDeServicio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return this.version;
    }

    public DespliegueDeServicio version(String version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Entorno getEntorno() {
        return this.entorno;
    }

    public DespliegueDeServicio entorno(Entorno entorno) {
        this.setEntorno(entorno);
        return this;
    }

    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
    }

    public Instant getIniciadoEn() {
        return this.iniciadoEn;
    }

    public DespliegueDeServicio iniciadoEn(Instant iniciadoEn) {
        this.setIniciadoEn(iniciadoEn);
        return this;
    }

    public void setIniciadoEn(Instant iniciadoEn) {
        this.iniciadoEn = iniciadoEn;
    }

    public Instant getFinalizadoEn() {
        return this.finalizadoEn;
    }

    public DespliegueDeServicio finalizadoEn(Instant finalizadoEn) {
        this.setFinalizadoEn(finalizadoEn);
        return this;
    }

    public void setFinalizadoEn(Instant finalizadoEn) {
        this.finalizadoEn = finalizadoEn;
    }

    public ResultadoDespliegue getResultado() {
        return this.resultado;
    }

    public DespliegueDeServicio resultado(ResultadoDespliegue resultado) {
        this.setResultado(resultado);
        return this;
    }

    public void setResultado(ResultadoDespliegue resultado) {
        this.resultado = resultado;
    }

    public Cambio getCambio() {
        return this.cambio;
    }

    public void setCambio(Cambio cambio) {
        this.cambio = cambio;
    }

    public DespliegueDeServicio cambio(Cambio cambio) {
        this.setCambio(cambio);
        return this;
    }

    public Servicio getServicio() {
        return this.servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public DespliegueDeServicio servicio(Servicio servicio) {
        this.setServicio(servicio);
        return this;
    }

    public User getEjecutadoPor() {
        return this.ejecutadoPor;
    }

    public void setEjecutadoPor(User user) {
        this.ejecutadoPor = user;
    }

    public DespliegueDeServicio ejecutadoPor(User user) {
        this.setEjecutadoPor(user);
        return this;
    }

    public Set<CorrelacionCambioIncidente> getCorrelacions() {
        return this.correlacions;
    }

    public void setCorrelacions(Set<CorrelacionCambioIncidente> correlacionCambioIncidentes) {
        if (this.correlacions != null) {
            this.correlacions.forEach(i -> i.setDespliegue(null));
        }
        if (correlacionCambioIncidentes != null) {
            correlacionCambioIncidentes.forEach(i -> i.setDespliegue(this));
        }
        this.correlacions = correlacionCambioIncidentes;
    }

    public DespliegueDeServicio correlacions(Set<CorrelacionCambioIncidente> correlacionCambioIncidentes) {
        this.setCorrelacions(correlacionCambioIncidentes);
        return this;
    }

    public DespliegueDeServicio addCorrelacion(CorrelacionCambioIncidente correlacionCambioIncidente) {
        this.correlacions.add(correlacionCambioIncidente);
        correlacionCambioIncidente.setDespliegue(this);
        return this;
    }

    public DespliegueDeServicio removeCorrelacion(CorrelacionCambioIncidente correlacionCambioIncidente) {
        this.correlacions.remove(correlacionCambioIncidente);
        correlacionCambioIncidente.setDespliegue(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DespliegueDeServicio)) {
            return false;
        }
        return getId() != null && getId().equals(((DespliegueDeServicio) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DespliegueDeServicio{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", entorno='" + getEntorno() + "'" +
            ", iniciadoEn='" + getIniciadoEn() + "'" +
            ", finalizadoEn='" + getFinalizadoEn() + "'" +
            ", resultado='" + getResultado() + "'" +
            "}";
    }
}
