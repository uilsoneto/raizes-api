package com.raizes.application.dto.response;

import com.raizes.domain.entity.Unidade;

public record UnidadeResponse(Long id, String nome, String cidade, String estado, boolean ativa) {
    public static UnidadeResponse from(Unidade u) {
        return new UnidadeResponse(u.getId(), u.getNome(), u.getCidade(), u.getEstado(), u.isAtiva());
    }
}
