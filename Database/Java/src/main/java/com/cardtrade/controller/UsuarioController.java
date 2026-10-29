package com.cardtrade.controller;
import com.cardtrade.dto.*;import com.cardtrade.service.UsuarioService;import jakarta.validation.Valid;import java.util.*;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/usuarios") @CrossOrigin(origins="*")
public class UsuarioController {private final UsuarioService s;public UsuarioController(UsuarioService s){this.s=s;}
 @PostMapping public ApiResponse<Map<String,Long>> crear(@Valid @RequestBody UsuarioRequest r){long id=s.crear(r);return ApiResponse.ok("Usuario insertado correctamente",Map.of("id",id));}
 @GetMapping public ApiResponse<List<Map<String,Object>>> listar(){return ApiResponse.ok("Usuarios obtenidos",s.listar());}}
