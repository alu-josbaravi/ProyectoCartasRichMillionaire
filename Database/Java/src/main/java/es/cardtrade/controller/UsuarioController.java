package es.cardtrade.controller;
import es.cardtrade.dto.*; import es.cardtrade.service.UsuarioService; import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/usuarios")
public class UsuarioController {
 private final UsuarioService service; public UsuarioController(UsuarioService s){service=s;}
 @GetMapping public ApiResponse listar(){return ApiResponse.ok("Usuarios obtenidos",service.listar());}
 @PostMapping public ResponseEntity<ApiResponse> crear(@Valid @RequestBody UsuarioRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Usuario y carrito creados correctamente",service.crear(r)));}
}
