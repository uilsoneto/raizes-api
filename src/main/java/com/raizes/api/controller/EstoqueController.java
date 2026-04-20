package com.raizes.api.controller;

import com.raizes.application.dto.request.MovimentacaoEstoqueRequest;
import com.raizes.application.dto.response.EstoqueResponse;
import com.raizes.application.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
@Tag(name = "Estoque")
@SecurityRequirement(name = "bearerAuth")
public class EstoqueController {

    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    @GetMapping("/unidades/{unidadeId}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','ATENDENTE')")
    @Operation(summary = "Consultar estoque de uma unidade")
    public ResponseEntity<List<EstoqueResponse>> listar(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(service.listarPorUnidade(unidadeId));
    }

    @PostMapping("/unidades/{unidadeId}/movimentar")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
    @Operation(summary = "Movimentar estoque (ENTRADA ou SAIDA)")
    public ResponseEntity<EstoqueResponse> movimentar(@PathVariable Long unidadeId,
                                                       @Valid @RequestBody MovimentacaoEstoqueRequest req) {
        return ResponseEntity.ok(service.movimentar(unidadeId, req));
    }
}
