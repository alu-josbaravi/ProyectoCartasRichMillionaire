package es.cardtrade.service;
import java.io.*; import java.math.*; import java.nio.charset.StandardCharsets; import java.sql.*; import java.time.*; import java.time.format.DateTimeFormatter; import java.util.*;
import javax.sql.DataSource;
import org.springframework.stereotype.Service;
@Service
public class SqlExportService {
 private static final List<String> TABLAS=List.of("Usuario","Producto","Direccion","Carrito","DetalleCarrito","Pedido","DetallePedido","Pago","Favorito","Incidencia");
 private final DataSource ds; public SqlExportService(DataSource ds){this.ds=ds;}
 public byte[] generar() throws SQLException {
  StringBuilder out=new StringBuilder("USE tienda_cartas;\n\nSET FOREIGN_KEY_CHECKS = 0;\nSTART TRANSACTION;\n\n");
  try(Connection c=ds.getConnection()){
   for(String tabla:TABLAS) exportarTabla(c,tabla,out);
  }
  out.append("COMMIT;\nSET FOREIGN_KEY_CHECKS = 1;\n");
  return out.toString().getBytes(StandardCharsets.UTF_8);
 }
 private void exportarTabla(Connection c,String tabla,StringBuilder out)throws SQLException{
  try(Statement st=c.createStatement(); ResultSet rs=st.executeQuery("SELECT * FROM `"+tabla+"` ORDER BY 1")){
   ResultSetMetaData md=rs.getMetaData(); int n=md.getColumnCount();
   while(rs.next()){
    out.append("INSERT INTO `").append(tabla).append("` (");
    for(int i=1;i<=n;i++){if(i>1)out.append(", ");out.append('`').append(md.getColumnLabel(i)).append('`');}
    out.append(") VALUES (");
    for(int i=1;i<=n;i++){if(i>1)out.append(", ");out.append(sqlValor(rs.getObject(i)));}
    out.append(");\n");
   }
   out.append('\n');
  }
 }
 private String sqlValor(Object v){
  if(v==null)return "NULL";
  if(v instanceof Boolean b)return b?"TRUE":"FALSE";
  if(v instanceof Number)return v.toString();
  if(v instanceof byte[] bytes)return "X'"+java.util.HexFormat.of().formatHex(bytes)+"'";
  if(v instanceof Timestamp t)return texto(t.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  if(v instanceof java.sql.Date d)return texto(d.toLocalDate().toString());
  if(v instanceof LocalDateTime d)return texto(d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  return texto(v.toString());
 }
 private String texto(String s){return "'"+s.replace("\","\\").replace("'","''")+"'";}
}
