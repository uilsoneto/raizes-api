package com.raizes.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProdutoRequest(
        @NotBlank String nome,
        String descricao,
        @Positive Double preco,
        @NotBlank String categoria
) {}
