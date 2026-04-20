package com.raizes.application.dto.response;

import com.raizes.domain.entity.Fidelidade;

public record FidelidadeResponse(Long usuarioId, String nomeUsuario, Integer pontos) {
    public static FidelidadeResponse from(Fidelidade f) {
        return new FidelidadeResponse(f.getUsuario().getId(), f.getUsuario().getNome(), f.getPontos());
    }
}
