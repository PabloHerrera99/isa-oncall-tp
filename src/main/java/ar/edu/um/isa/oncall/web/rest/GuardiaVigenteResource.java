package ar.edu.um.isa.oncall.web.rest;

import ar.edu.um.isa.oncall.service.GuardiaVigenteService;
import ar.edu.um.isa.oncall.service.dto.GuardiaVigenteDTO;
import java.time.Clock;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * GET /api/guardia-vigente?rotacionId=1&en=2026-12-01T11:00:00Z
 */
@RestController
@RequestMapping("/api/guardia-vigente")
public class GuardiaVigenteResource {

    private final GuardiaVigenteService guardiaVigenteService;
    private final Clock clock;

    public GuardiaVigenteResource(GuardiaVigenteService guardiaVigenteService, Clock clock) {
        this.guardiaVigenteService = guardiaVigenteService;
        this.clock = clock;
    }

    @GetMapping
    public ResponseEntity<GuardiaVigenteDTO> consultar(@RequestParam Long rotacionId, @RequestParam(required = false) Instant en) {
        Instant instante = en != null ? en : clock.instant();
        return ResponseEntity.ok(guardiaVigenteService.consultar(rotacionId, instante));
    }
}
