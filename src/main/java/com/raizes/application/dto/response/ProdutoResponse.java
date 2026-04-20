package com.raizes.application.dto.response;

import com.raizes.domain.entity.Produto;

public record ProdutoResponse(Long id, String nome, String descricao, Double preco, String categoria, boolean disponivel) {
    public static ProdutoResponse from(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getDescricao(), p.getPreco(), p.getCategoria(), p.isDisponivel());
    }
}
