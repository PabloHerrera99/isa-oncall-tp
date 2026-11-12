package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.service.EscalamientoService;
import ar.edu.um.isa.oncall.service.dto.EscalonDTO;
import ar.edu.um.isa.oncall.service.dto.EstadoDeCorteDTO;
import ar.edu.um.isa.oncall.service.dto.ResultadoEscalamientoDTO;
import ar.edu.um.isa.oncall.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/escalamientos")
public class EscalamientoResource {

    private final EscalamientoService escalamientoService;

    public EscalamientoResource(EscalamientoService escalamientoService) {
        this.escalamientoService = escalamientoService;
    }

    /** Evalua el incidente ahora, sin esperar al scheduler. */
    @PostMapping("/{incidenteId}/evaluar")
    public ResponseEntity<ResultadoEscalamientoDTO> evaluar(@PathVariable Long incidenteId) {
        return ResponseUtil.wrapOrNotFound(escalamientoService.evaluar(incidenteId));
    }

    /** Historial de escalones ejecutados sobre el incidente. */
    @GetMapping("/{incidenteId}")
    public List<EscalonDTO> historial(@PathVariable Long incidenteId) {
        return escalamientoService.historial(incidenteId);
    }

    /** Reconocer el incidente: estado de corte. */
    @PostMapping("/{incidenteId}/reconocer")
    public ResponseEntity<EstadoDeCorteDTO> reconocer(@PathVariable Long incidenteId) {
        return ResponseUtil.wrapOrNotFound(escalamientoService.reconocer(incidenteId));
    }

    /** Silenciar el escalamiento por unos minutos (1 a 1440): estado de corte temporal. */
    @PostMapping("/{incidenteId}/silenciar")
    public ResponseEntity<EstadoDeCorteDTO> silenciar(@PathVariable Long incidenteId, @RequestParam int minutos) {
        if (minutos < 1 || minutos > 1440) {
            throw new BadRequestAlertException("minutos tiene que estar entre 1 y 1440", "escalamiento", "minutosinvalidos");
        }
        return ResponseUtil.wrapOrNotFound(escalamientoService.silenciar(incidenteId, minutos));
    }
}
