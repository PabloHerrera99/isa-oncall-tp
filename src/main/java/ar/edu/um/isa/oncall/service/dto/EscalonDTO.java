package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.EjecucionDeEscalamiento;
import ar.edu.um.isa.oncall.domain.enumeration.MotivoDestinatario;
import ar.edu.um.isa.oncall.domain.enumeration.ResultadoEscalamiento;
import java.time.Instant;

public record EscalonDTO(
    Long id,
    int vuelta,
    int nivel,
    MotivoDestinatario motivo,
    ResultadoEscalamiento resultado,
    String destinatarioLogin,
    Instant ejecutadoEn,
    Instant proximoEn,
    String detalle
) {
    public static EscalonDTO de(EjecucionDeEscalamiento e) {
        return new EscalonDTO(
            e.getId(),
            e.getVuelta(),
            e.getNivel(),
            e.getMotivo(),
            e.getResultado(),
            e.getDestinatario() != null ? e.getDestinatario().getLogin() : null,
            e.getEjecutadoEn(),
            e.getProximoEn(),
            e.getDetalle()
        );
    }
}
