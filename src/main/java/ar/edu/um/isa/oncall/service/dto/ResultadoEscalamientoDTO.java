package ar.edu.um.isa.oncall.service.dto;

import java.time.Instant;
import java.util.List;

public record ResultadoEscalamientoDTO(
    Long incidenteId,
    Instant evaluadoEn,
    Accion accion,
    String detalle,
    List<EscalonDTO> escalonesEjecutados,
    Instant proximaEvaluacion
) {
    public enum Accion {
        ESCALADO,
        EN_ESPERA,
        CORTADO,
        TERMINADO,
    }
}
