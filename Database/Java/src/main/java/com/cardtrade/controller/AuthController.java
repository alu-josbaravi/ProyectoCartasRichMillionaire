package com.cardtrade.controller;

import com.cardtrade.dto.ApiResponse;
import com.cardtrade.dto.LoginRequest;
import com.cardtrade.service.AuthService;
import com.cardtrade.service.SesionService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService s;
    private final SesionService sesiones;

    public AuthController(AuthService s, SesionService sesiones) {
        this.s = s;
        this.sesiones = sesiones;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest r) {
        return ApiResponse.ok("Sesion iniciada", s.login(r));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String cabecera) {
        sesiones.cerrar(AuthInterceptor.extraerToken(cabecera));
        return new ApiResponse<>(true, "Sesion cerrada", null);
    }
}
