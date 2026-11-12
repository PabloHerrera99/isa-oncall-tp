package ar.edu.um.isa.oncall.service.dto;

import ar.edu.um.isa.oncall.domain.enumeration.EstadoIncidente;
import java.time.Instant;

public record EstadoDeCorteDTO(Long incidenteId, EstadoIncidente estado, Instant reconocidoEn, Instant silenciadoHasta) {}
