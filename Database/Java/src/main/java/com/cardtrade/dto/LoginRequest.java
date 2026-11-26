package com.cardtrade.dto;

import jakarta.validation.constraints.NotBlank;

/** "usuario" puede ser el nombre de usuario o el email. */
public record LoginRequest(
        @NotBlank String usuario,
        @NotBlank String password) {}
