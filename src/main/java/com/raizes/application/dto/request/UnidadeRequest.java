package com.raizes.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UnidadeRequest(
        @NotBlank String nome,
        @NotBlank String cidade,
        @NotBlank String estado
) {}
