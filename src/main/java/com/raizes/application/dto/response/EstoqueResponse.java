package com.raizes.application.dto.response;

import com.raizes.domain.entity.Estoque;

public record EstoqueResponse(Long id, Long unidadeId, String unidadeNome, Long produtoId, String produtoNome, Integer quantidade) {
    public static EstoqueResponse from(Estoque e) {
        return new EstoqueResponse(e.getId(), e.getUnidade().getId(), e.getUnidade().getNome(),
                e.getProduto().getId(), e.getProduto().getNome(), e.getQuantidade());
    }
}
