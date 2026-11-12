package ar.edu.um.isa.oncall.service.dto;

import java.time.Instant;

public record GuardiaVigenteDTO(
    Long rotacionId,
    Instant instante,
    boolean cubierta,
    Long turnoId,
    String responsableLogin,
    Boolean esReemplazo,
    int turnosSolapados
) {}
