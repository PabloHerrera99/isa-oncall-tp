package ar.edu.um.isa.oncall.service;

import ar.edu.um.isa.oncall.domain.TurnoDeGuardia;
import ar.edu.um.isa.oncall.repository.GuardiaVigenteRepository;
import ar.edu.um.isa.oncall.service.dto.GuardiaVigenteDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Resuelve quien esta de guardia en una rotacion en un instante dado.
 */
@Service
@Transactional(readOnly = true)
public class GuardiaVigenteService {

    private final GuardiaVigenteRepository guardiaVigenteRepository;

    public GuardiaVigenteService(GuardiaVigenteRepository guardiaVigenteRepository) {
        this.guardiaVigenteRepository = guardiaVigenteRepository;
    }

    /**
     * El turno que manda en ese instante, o vacio si hay un hueco.
     */
    public Optional<TurnoDeGuardia> turnoVigente(Long rotacionId, Instant instante) {
        return guardiaVigenteRepository.findTurnosVigentes(rotacionId, instante).stream().findFirst();
    }

    public GuardiaVigenteDTO consultar(Long rotacionId, Instant instante) {
        List<TurnoDeGuardia> vigentes = guardiaVigenteRepository.findTurnosVigentes(rotacionId, instante);
        if (vigentes.isEmpty()) {
            return new GuardiaVigenteDTO(rotacionId, instante, false, null, null, null, 0);
        }
        TurnoDeGuardia turno = vigentes.get(0);
        return new GuardiaVigenteDTO(
            rotacionId,
            instante,
            true,
            turno.getId(),
            turno.getResponsable().getLogin(),
            Boolean.TRUE.equals(turno.getEsReemplazo()),
            vigentes.size()
        );
    }
}
