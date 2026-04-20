package com.raizes.api.controller;

import com.raizes.application.dto.response.PagamentoResponse;
import com.raizes.application.service.PagamentoService;
import com.raizes.domain.enums.FormaPagamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos")
@Tag(name = "Pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping("/pedidos/{pedidoId}")
    @PreAuthorize("hasAnyRole('CLIENTE','ATENDENTE','ADMIN')")
    @Operation(summary = "Processar pagamento mock para um pedido")
    public ResponseEntity<PagamentoResponse> processar(@PathVariable Long pedidoId,
                                                        @RequestParam FormaPagamento forma) {
        return ResponseEntity.ok(service.processar(pedidoId, forma));
    }

    @GetMapping("/pedidos/{pedidoId}")
    @Operation(summary = "Consultar pagamento de um pedido")
    public ResponseEntity<PagamentoResponse> consultar(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(service.consultar(pedidoId));
    }
}
