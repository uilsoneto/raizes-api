package com.raizes.application.dto.request;

import com.raizes.domain.enums.CanalPedido;
import com.raizes.domain.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoRequest(
        @NotNull Long unidadeId,
        @NotNull CanalPedido canalPedido,
        @NotNull FormaPagamento formaPagamento,
        @NotEmpty @Valid List<ItemPedidoRequest> itens
) {
    public record ItemPedidoRequest(
            @NotNull Long produtoId,
            @NotNull @Min(1) Integer quantidade
    ) {}
}
