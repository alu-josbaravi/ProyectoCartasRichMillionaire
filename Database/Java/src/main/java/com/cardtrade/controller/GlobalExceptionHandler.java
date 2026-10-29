package com.cardtrade.controller;
import com.cardtrade.dto.ApiResponse;import java.util.stream.Collectors;import org.springframework.dao.DataIntegrityViolationException;import org.springframework.http.*;import org.springframework.web.bind.MethodArgumentNotValidException;import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiResponse<Void>> validacion(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(Collectors.joining("; "));return ResponseEntity.badRequest().body(ApiResponse.error(m));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ApiResponse<Void>> integridad(){return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("Valor duplicado o restriccion de base de datos incumplida"));}
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiResponse<Void>> general(Exception e){

        e.printStackTrace();

        return ResponseEntity.status(500)
                .body(ApiResponse.error("Error interno: " + e.getMessage()));
    }
}
