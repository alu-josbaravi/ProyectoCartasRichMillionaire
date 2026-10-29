package com.cardtrade.controller;
import com.cardtrade.dto.*;import com.cardtrade.service.ProductoService;import jakarta.validation.Valid;import java.util.*;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/productos") @CrossOrigin(origins="*")
public class ProductoController {private final ProductoService s;public ProductoController(ProductoService s){this.s=s;}
 @PostMapping public ApiResponse<Map<String,Long>> crear(@Valid @RequestBody ProductoRequest r){long id=s.crear(r);return ApiResponse.ok("Producto insertado correctamente",Map.of("id",id));}
 @GetMapping public ApiResponse<List<Map<String,Object>>> listar(){return ApiResponse.ok("Productos obtenidos",s.listar());}}
