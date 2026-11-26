package com.cardtrade.controller;

import com.cardtrade.dto.ApiResponse;
import com.cardtrade.dto.FavoritoRequest;
import com.cardtrade.service.FavoritoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favoritos")
@CrossOrigin(origins = "*")
public class FavoritoController {

    private final FavoritoService s;

    public FavoritoController(FavoritoService s) {
        this.s = s;
    }

    @GetMapping("/{usuarioId}")
    public ApiResponse<List<String>> listar(@PathVariable long usuarioId) {
        return ApiResponse.ok("Favoritos obtenidos", s.listar(usuarioId));
    }

    @PostMapping("/{usuarioId}")
    public ApiResponse<List<String>> anadir(@PathVariable long usuarioId,
                                            @Valid @RequestBody FavoritoRequest r) {
        return ApiResponse.ok("Favorito anadido", s.anadir(usuarioId, r.cartaExternaId()));
    }

    // El id de la carta va como parametro (?cartaExternaId=...) para evitar problemas con caracteres en la ruta
    @DeleteMapping("/{usuarioId}")
    public ApiResponse<List<String>> quitar(@PathVariable long usuarioId,
                                            @RequestParam String cartaExternaId) {
        return ApiResponse.ok("Favorito eliminado", s.quitar(usuarioId, cartaExternaId));
    }
}
