package com.cardtrade.controller;

import com.cardtrade.dto.ApiResponse;
import com.cardtrade.dto.CantidadRequest;
import com.cardtrade.dto.CarritoItemRequest;
import com.cardtrade.service.CarritoService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    private final CarritoService s;

    public CarritoController(CarritoService s) {
        this.s = s;
    }

    @GetMapping("/{usuarioId}")
    public ApiResponse<Map<String, Object>> obtener(@PathVariable long usuarioId) {
        return ApiResponse.ok("Carrito obtenido", s.obtener(usuarioId));
    }

    @PostMapping("/{usuarioId}/items")
    public ApiResponse<Map<String, Object>> anadir(@PathVariable long usuarioId,
                                                   @Valid @RequestBody CarritoItemRequest r) {
        return ApiResponse.ok("Producto anadido al carrito",
                s.anadir(usuarioId, r.productoId(), r.cantidad()));
    }

    @PutMapping("/{usuarioId}/items/{productoId}")
    public ApiResponse<Map<String, Object>> cambiarCantidad(@PathVariable long usuarioId,
                                                            @PathVariable long productoId,
                                                            @Valid @RequestBody CantidadRequest r) {
        return ApiResponse.ok("Cantidad actualizada",
                s.cambiarCantidad(usuarioId, productoId, r.cantidad()));
    }

    @DeleteMapping("/{usuarioId}/items/{productoId}")
    public ApiResponse<Map<String, Object>> quitar(@PathVariable long usuarioId,
                                                   @PathVariable long productoId) {
        return ApiResponse.ok("Producto eliminado del carrito", s.quitar(usuarioId, productoId));
    }
}
