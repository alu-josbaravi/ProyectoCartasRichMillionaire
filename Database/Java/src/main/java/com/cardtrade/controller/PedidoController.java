package com.cardtrade.controller;

import com.cardtrade.dto.ApiResponse;
import com.cardtrade.dto.PedidoRequest;
import com.cardtrade.service.ApiException;
import com.cardtrade.service.PedidoService;
import com.cardtrade.service.SesionService.Sesion;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    private final PedidoService s;

    public PedidoController(PedidoService s) {
        this.s = s;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> crear(
            @Valid @RequestBody PedidoRequest r,
            @RequestAttribute(name = AuthInterceptor.ATRIBUTO, required = false) Sesion sesion) {

        if (sesion == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesion");
        }
        if (!sesion.esAdmin() && sesion.usuarioId() != r.usuarioId()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "No puedes crear pedidos para otro usuario");
        }
        return ApiResponse.ok("Pedido realizado correctamente", s.crear(r));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ApiResponse<List<Map<String, Object>>> listar(@PathVariable("usuarioId") long usuarioId) {
        return ApiResponse.ok("Pedidos obtenidos", s.listarPorUsuario(usuarioId));
    }
}
