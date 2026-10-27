package es.cardtrade.controller;
import es.cardtrade.dto.*; import es.cardtrade.service.ProductoService; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/productos")
public class ProductoController {
 private final ProductoService service; public ProductoController(ProductoService s){service=s;}
 @GetMapping public ApiResponse listar(){return ApiResponse.ok("Productos obtenidos",service.listar());}
 @PostMapping public ResponseEntity<ApiResponse> crear(@Valid @RequestBody ProductoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Producto insertado correctamente",service.crear(r)));}
}
