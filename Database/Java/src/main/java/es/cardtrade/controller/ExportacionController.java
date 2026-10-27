package es.cardtrade.controller;
import es.cardtrade.service.SqlExportService; import java.sql.SQLException; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/exportacion")
public class ExportacionController {
 private final SqlExportService service; public ExportacionController(SqlExportService s){service=s;}
 @GetMapping(value="/inserts",produces="application/sql")
 public ResponseEntity<byte[]> descargar() throws SQLException {return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=inserts.sql").contentType(MediaType.parseMediaType("application/sql;charset=UTF-8")).body(service.generar());}
}
