package com.cardtrade.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CarritoItemRequest(
        @NotNull Long productoId,
        @NotNull @Min(1) Integer cantidad) {}
