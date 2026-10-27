package es.cardtrade.service;
import es.cardtrade.dto.UsuarioRequest;
import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UsuarioService {
 private static final Set<String> ROLES=Set.of("ADMINISTRADOR","CLIENTE");
 private final JdbcClient db; private final BCryptPasswordEncoder encoder;
 public UsuarioService(JdbcClient db, BCryptPasswordEncoder encoder){this.db=db;this.encoder=encoder;}
 @Transactional
 public Map<String,Object> crear(UsuarioRequest r){
  String rol=(r.rol()==null||r.rol().isBlank())?"CLIENTE":r.rol().toUpperCase();
  if(!ROLES.contains(rol)) throw new IllegalArgumentException("El rol debe ser ADMINISTRADOR o CLIENTE");
  String tel=(r.telefono()==null||r.telefono().isBlank())?null:r.telefono().trim();
  var key=db.sql("INSERT INTO Usuario(usuario,email,telefono,password,rol) VALUES(:u,:e,:t,:p,:r)")
   .param("u",r.usuario().trim()).param("e",r.email().trim()).param("t",tel,Types.VARCHAR)
   .param("p",encoder.encode(r.password())).param("r",rol).updateAndReturnGeneratedKey();
  long id=key.longValue(); crearCarrito(id);
  return Map.of("id",id,"usuario",r.usuario().trim(),"email",r.email().trim(),"rol",rol);
 }
 private void crearCarrito(long usuarioId){db.sql("INSERT INTO Carrito(usuario_id) VALUES(:id)").param("id",usuarioId).update();}
 public List<Map<String,Object>> listar(){return db.sql("SELECT id,usuario,email,telefono,fecha_registro,rol FROM Usuario ORDER BY id").query().listOfRows();}
}
