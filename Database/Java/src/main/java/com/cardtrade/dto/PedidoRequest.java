package com.cardtrade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Map;

/**
 * Datos para crear un pedido a partir del carrito del usuario.
 * "cartas" es opcional: el frontend envia el nombre, set y numero de cada carta
 * (que vienen de la API externa) para guardar la copia historica en DetallePedido.
 */
public record PedidoRequest(
        @NotNull Long usuarioId,
        @NotBlank @Size(max = 120) String nombreDestinatario,
        @NotBlank @Size(max = 150) String calle,
        @NotBlank @Size(max = 20) String numero,
        @Size(max = 20) String piso,
        @NotBlank @Size(max = 80) String ciudad,
        @NotBlank @Size(max = 80) String provincia,
        @NotBlank @Size(max = 12) String codigoPostal,
        @NotBlank @Size(max = 80) String pais,
        @NotBlank @Pattern(regexp = "TARJETA|PAYPAL|TRANSFERENCIA") String metodoPago,
        Map<String, DatosCarta> cartas) {

    public record DatosCarta(String nombre, String set, String numero) {}
}
