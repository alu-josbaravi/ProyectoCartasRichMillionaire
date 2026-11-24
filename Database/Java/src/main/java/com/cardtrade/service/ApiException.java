package com.cardtrade.service;

import org.springframework.http.HttpStatus;

/** Error controlado: el GlobalExceptionHandler lo convierte en {correcto:false, mensaje}. */
public class ApiException extends RuntimeException {

    private final HttpStatus estado;

    public ApiException(HttpStatus estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public HttpStatus getEstado() {
        return estado;
    }
}
