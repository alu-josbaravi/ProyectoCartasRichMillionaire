package com.cardtrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FavoritoRequest(@NotBlank @Size(max = 100) String cartaExternaId) {}
