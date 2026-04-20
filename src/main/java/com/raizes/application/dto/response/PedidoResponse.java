package com.raizes.application.dto.response;

import com.raizes.domain.entity.ItemPedido;
import com.raizes.domain.entity.Pedido;
import com.raizes.domain.enums.CanalPedido;
import com.raizes.domain.enums.StatusPedido;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long usuarioId,
        Long unidadeId,
        CanalPedido canalPedido,
        StatusPedido status,
        Double total,
        LocalDateTime criadoEm,
        List<ItemResponse> itens
) {
    public record ItemResponse(Long produtoId, String produtoNome, Integer quantidade, Double precoUnitario) {}

    public static PedidoResponse from(Pedido p) {
        List<ItemResponse> itens = p.getItens().stream()
                .map(i -> new ItemResponse(i.getProduto().getId(), i.getProduto().getNome(),
                        i.getQuantidade(), i.getPrecoUnitario()))
                .toList();
        return new PedidoResponse(p.getId(), p.getUsuario().getId(), p.getUnidade().getId(),
                p.getCanalPedido(), p.getStatus(), p.getTotal(), p.getCriadoEm(), itens);
    }
}
