package com.cardtrade.controller;

import com.cardtrade.dto.*;
import com.cardtrade.service.ApiException;
import com.cardtrade.service.SesionService.Sesion;
import com.cardtrade.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService s;

    public UsuarioController(UsuarioService s) {
        this.s = s;
    }

    @PostMapping
    public ApiResponse<Map<String, Long>> crear(
            @Valid @RequestBody UsuarioRequest r,
            @RequestAttribute(name = AuthInterceptor.ATRIBUTO, required = false) Sesion sesion) {

        // El registro es publico: solo un administrador puede crear otro administrador
        boolean pideAdmin = "ADMINISTRADOR".equals(r.rol());
        if (pideAdmin && (sesion == null || !sesion.esAdmin())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Solo un administrador puede crear administradores");
        }

        long id = s.crear(r);
        return ApiResponse.ok("Usuario insertado correctamente", Map.of("id", id));
    }

    // Protegido por el AuthInterceptor: solo administradores
    @GetMapping
    public ApiResponse<List<Map<String, Object>>> listar() {
        return ApiResponse.ok("Usuarios obtenidos", s.listar());
    }
}
