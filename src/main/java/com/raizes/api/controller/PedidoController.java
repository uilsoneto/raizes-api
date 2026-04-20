package com.raizes.api.controller;

import com.raizes.application.dto.request.AtualizarStatusRequest;
import com.raizes.application.dto.request.PedidoRequest;
import com.raizes.application.dto.response.PedidoResponse;
import com.raizes.application.service.PedidoService;
import com.raizes.domain.enums.CanalPedido;
import com.raizes.domain.enums.StatusPedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos")
@SecurityRequirement(name = "bearerAuth")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE','ATENDENTE','ADMIN')")
    @Operation(summary = "Criar pedido")
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req,
                                                 @AuthenticationPrincipal String email) {
        return ResponseEntity.status(201).body(service.criar(email, req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID")
    public ResponseEntity<PedidoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','COZINHA','ATENDENTE')")
    @Operation(summary = "Listar pedidos com filtros opcionais por canal e status")
    public ResponseEntity<Page<PedidoResponse>> listar(
            @RequestParam(required = false) CanalPedido canalPedido,
            @RequestParam(required = false) StatusPedido status,
            Pageable pageable) {
        return ResponseEntity.ok(service.listar(canalPedido, status, pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','COZINHA','ATENDENTE')")
    @Operation(summary = "Atualizar status do pedido")
    public ResponseEntity<PedidoResponse> atualizarStatus(@PathVariable Long id,
                                                            @Valid @RequestBody AtualizarStatusRequest req,
                                                            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(service.atualizarStatus(id, req, email));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GERENTE','CLIENTE')")
    @Operation(summary = "Cancelar pedido")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id,
                                                    @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(service.cancelar(id, email));
    }
}
