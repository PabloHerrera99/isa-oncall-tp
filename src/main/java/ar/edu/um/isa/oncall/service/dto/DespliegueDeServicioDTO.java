package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.Entorno;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoDespliegue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link ar.edu.um.isa.oncall.domain.DespliegueDeServicio} entity.
 */
@Schema(
    description = "La llegada concreta de un cambio a un servicio. Lo que se correlaciona\ncon un incidente es el despliegue, no el cambio en abstracto."
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DespliegueDeServicioDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 60)
    private String version;

    @NotNull
    private Entorno entorno;

    @NotNull
    private Instant iniciadoEn;

    private Instant finalizadoEn;

    @NotNull
    private ResultadoDespliegue resultado;

    @NotNull
    private CambioDTO cambio;

    @NotNull
    private ServicioDTO servicio;

    private UserDTO ejecutadoPor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Entorno getEntorno() {
        return entorno;
    }

    public void setEntorno(Entorno entorno) {
        this.entorno = entorno;
    }

    public Instant getIniciadoEn() {
        return iniciadoEn;
    }

    public void setIniciadoEn(Instant iniciadoEn) {
        this.iniciadoEn = iniciadoEn;
    }

    public Instant getFinalizadoEn() {
        return finalizadoEn;
    }

    public void setFinalizadoEn(Instant finalizadoEn) {
        this.finalizadoEn = finalizadoEn;
    }

    public ResultadoDespliegue getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoDespliegue resultado) {
        this.resultado = resultado;
    }

    public CambioDTO getCambio() {
        return cambio;
    }

    public void setCambio(CambioDTO cambio) {
        this.cambio = cambio;
    }

    public ServicioDTO getServicio() {
        return servicio;
    }

    public void setServicio(ServicioDTO servicio) {
        this.servicio = servicio;
    }

    public UserDTO getEjecutadoPor() {
        return ejecutadoPor;
    }

    public void setEjecutadoPor(UserDTO ejecutadoPor) {
        this.ejecutadoPor = ejecutadoPor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DespliegueDeServicioDTO)) {
            return false;
        }

        DespliegueDeServicioDTO despliegueDeServicioDTO = (DespliegueDeServicioDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, despliegueDeServicioDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DespliegueDeServicioDTO{" +
            "id=" + getId() +
            ", version='" + getVersion() + "'" +
            ", entorno='" + getEntorno() + "'" +
            ", iniciadoEn='" + getIniciadoEn() + "'" +
            ", finalizadoEn='" + getFinalizadoEn() + "'" +
            ", resultado='" + getResultado() + "'" +
            ", cambio=" + getCambio() +
            ", servicio=" + getServicio() +
            ", ejecutadoPor=" + getEjecutadoPor() +
            "}";
    }
}
