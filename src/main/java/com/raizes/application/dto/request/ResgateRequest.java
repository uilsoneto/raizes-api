package com.raizes.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ResgateRequest(@NotNull @Min(1) Integer pontos) {}
