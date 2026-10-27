package es.cardtrade.exception;
import es.cardtrade.dto.ApiResponse; import java.util.stream.Collectors; import org.springframework.dao.DataIntegrityViolationException; import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiResponse> validation(MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).collect(Collectors.joining("; "));return ResponseEntity.badRequest().body(ApiResponse.error(m));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ApiResponse> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<ApiResponse> duplicate(DataIntegrityViolationException e){return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error("No se pudo insertar: hay un valor duplicado o se incumple una restricción de la base de datos"));}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiResponse> other(Exception e){return ResponseEntity.status(500).body(ApiResponse.error("Error interno: "+e.getMessage()));}
}
