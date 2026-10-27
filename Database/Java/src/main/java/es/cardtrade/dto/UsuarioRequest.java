package es.cardtrade.dto;
import jakarta.validation.constraints.*;
public record UsuarioRequest(
 @NotBlank @Size(max=16) String usuario,
 @NotBlank @Email @Size(max=255) String email,
 @Size(max=20) String telefono,
 @NotBlank @Size(min=8,max=72) String password,
 String rol) {}
