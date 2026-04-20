package com.raizes.api.controller;

import com.raizes.application.dto.request.ResgateRequest;
import com.raizes.application.dto.response.FidelidadeResponse;
import com.raizes.application.service.FidelidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fidelidade")
@Tag(name = "Fidelidade")
@SecurityRequirement(name = "bearerAuth")
public class FidelidadeController {

    private final FidelidadeService service;

    public FidelidadeController(FidelidadeService service) {
        this.service = service;
    }

    @GetMapping("/usuarios/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','CLIENTE')")
    @Operation(summary = "Consultar saldo de pontos do usuário")
    public ResponseEntity<FidelidadeResponse> consultar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.consultar(usuarioId));
    }

    @PostMapping("/usuarios/{usuarioId}/resgatar")
    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    @Operation(summary = "Resgatar pontos de fidelidade")
    public ResponseEntity<FidelidadeResponse> resgatar(@PathVariable Long usuarioId,
                                                        @Valid @RequestBody ResgateRequest req) {
        return ResponseEntity.ok(service.resgatar(usuarioId, req));
    }
}
