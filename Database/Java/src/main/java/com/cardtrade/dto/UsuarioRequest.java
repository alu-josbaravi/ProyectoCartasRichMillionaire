package com.cardtrade.dto;
import jakarta.validation.constraints.*;
public record UsuarioRequest(
 @NotBlank @Size(max=50) String usuario,
 @NotBlank @Email @Size(max=120) String email,
 @Pattern(regexp="^$|^[0-9+ -]{7,20}$") String telefono,
 @NotBlank @Size(min=8,max=100) String password,
 @Pattern(regexp="ADMINISTRADOR|CLIENTE") String rol) {}
