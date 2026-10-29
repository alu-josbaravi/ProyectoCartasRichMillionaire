package com.cardtrade.service;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
@Service
public class ExportacionService {
 private final JdbcClient jdbc; public ExportacionService(JdbcClient jdbc){this.jdbc=jdbc;}
 private static final List<String> TABLAS=List.of("Usuario","Direccion","Carrito","Producto","DetalleCarrito","Pedido","DetallePedido","Pago","Favorito","Incidencia");
 public String exportar(){StringBuilder out=new StringBuilder("SET NAMES utf8mb4;\nSET FOREIGN_KEY_CHECKS=0;\n\n");
  for(String tabla:TABLAS){List<Map<String,Object>> rows=jdbc.sql("SELECT * FROM "+tabla).query().listOfRows();
   for(Map<String,Object> row:rows){out.append("INSERT INTO `").append(tabla).append("` (`").append(String.join("`,`",row.keySet())).append("`) VALUES (");
    out.append(row.values().stream().map(this::sqlValue).reduce((a,b)->a+","+b).orElse(""));out.append(");\n");} out.append("\n");}
  return out.append("SET FOREIGN_KEY_CHECKS=1;\n").toString();}
 private String sqlValue(Object v){if(v==null)return "NULL";if(v instanceof Number||v instanceof Boolean)return v.toString();return "'"+v.toString().replace("\\","\\\\").replace("'","''")+"'";}
}
