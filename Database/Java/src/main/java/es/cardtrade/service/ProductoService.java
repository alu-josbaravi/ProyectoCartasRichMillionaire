package es.cardtrade.service;
import es.cardtrade.dto.ProductoRequest;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ProductoService {
 private static final Set<String> ESTADOS=Set.of("NUEVA","EXCELENTE","BUENA","JUGADA","DETERIORADA");
 private static final Set<String> ACABADOS=Set.of("NORMAL","HOLO","REVERSE_HOLO");
 private final JdbcClient db; public ProductoService(JdbcClient db){this.db=db;}
 @Transactional public Map<String,Object> crear(ProductoRequest r){
  String e=r.estadoCarta().toUpperCase(), a=r.acabado().toUpperCase();
  if(!ESTADOS.contains(e)) throw new IllegalArgumentException("Estado de carta no válido");
  if(!ACABADOS.contains(a)) throw new IllegalArgumentException("Acabado no válido");
  boolean activo=r.activo()==null||r.activo();
  Number key=db.sql("INSERT INTO Producto(carta_externa_id,idioma,estado_carta,acabado,precio,stock,activo) VALUES(:c,:i,:e,:a,:p,:s,:v)")
   .param("c",r.cartaExternaId().trim()).param("i",r.idioma().trim().toLowerCase()).param("e",e).param("a",a)
   .param("p",r.precio()).param("s",r.stock()).param("v",activo).updateAndReturnGeneratedKey();
  return Map.of("id",key.longValue(),"cartaExternaId",r.cartaExternaId().trim(),"precio",r.precio(),"stock",r.stock());
 }
 public List<Map<String,Object>> listar(){return db.sql("SELECT * FROM Producto ORDER BY id").query().listOfRows();}
}
