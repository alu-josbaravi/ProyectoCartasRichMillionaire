package com.cardtrade.controller;

import com.cardtrade.dto.ApiResponse;
import com.cardtrade.service.AdminService;
import com.cardtrade.service.ApiException;
import com.cardtrade.service.SesionService.Sesion;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService s;

    public AdminController(AdminService s) {
        this.s = s;
    }

    @GetMapping("/tablas")
    public ApiResponse<List<Map<String, Object>>> tablas(
            @RequestAttribute(name = AuthInterceptor.ATRIBUTO, required = false) Sesion sesion) {
        exigirAdmin(sesion);
        return ApiResponse.ok("Tablas obtenidas", s.tablas());
    }

    @GetMapping("/tablas/{nombre}")
    public ApiResponse<Map<String, Object>> contenido(
            @PathVariable("nombre") String nombre,
            @RequestParam(name = "limite", defaultValue = "100") int limite,
            @RequestAttribute(name = AuthInterceptor.ATRIBUTO, required = false) Sesion sesion) {
        exigirAdmin(sesion);
        return ApiResponse.ok("Contenido de la tabla " + nombre, s.contenido(nombre, limite));
    }

    // El interceptor ya filtra /api/admin/**; esta comprobacion es una segunda barrera
    private void exigirAdmin(Sesion sesion) {
        if (sesion == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesion");
        }
        if (!sesion.esAdmin()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Se necesita ser administrador");
        }
    }
}
