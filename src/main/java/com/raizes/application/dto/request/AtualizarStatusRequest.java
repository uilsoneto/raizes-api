package com.raizes.application.dto.request;

import com.raizes.domain.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(@NotNull StatusPedido status) {}
