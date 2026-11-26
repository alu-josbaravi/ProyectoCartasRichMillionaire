package com.cardtrade.controller;

import com.cardtrade.dto.ApiResponse;
import com.cardtrade.service.ApiException;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> validacion(MethodArgumentNotValidException e) {
        String m = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + ": " + x.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ApiResponse.error(m));
    }

    // Errores de negocio controlados (login incorrecto, sin permisos, sin stock, carrito vacio...)
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiResponse<Void>> negocio(ApiException e) {
        return ResponseEntity.status(e.getEstado()).body(ApiResponse.error(e.getMessage()));
    }

    // Ruta que no existe: 404 de verdad, no un "error interno"
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ApiResponse<Void>> noEncontrado(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Recurso no encontrado"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<Void>> metodo(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(ApiResponse.error("Metodo HTTP no permitido"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse<Void>> jsonIncorrecto(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error("El cuerpo de la peticion no es un JSON valido"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse<Void>> tipoIncorrecto(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest().body(ApiResponse.error("Parametro incorrecto: " + e.getName()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<Void>> integridad() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error("Valor duplicado o restriccion de base de datos incumplida"));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> general(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500)
                .body(ApiResponse.error("Error interno: " + e.getMessage()));
    }
}
