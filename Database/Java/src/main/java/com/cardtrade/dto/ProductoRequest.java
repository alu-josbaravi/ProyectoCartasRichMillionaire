package com.cardtrade.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ProductoRequest(
 @NotBlank @Size(max=100) String cartaExternaId,
 @NotBlank @Size(max=10) String idioma,
 @NotBlank @Pattern(regexp="NUEVA|EXCELENTE|BUENA|ACEPTABLE|DANADA") String estadoCarta,
 @NotBlank @Pattern(regexp="NORMAL|HOLO|REVERSE_HOLO") String acabado,
 @NotNull @DecimalMin("0.00") BigDecimal precio,
 @NotNull @Min(0) Integer stock,
 Boolean activo) {}
