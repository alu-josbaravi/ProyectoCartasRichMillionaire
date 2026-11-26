package com.cardtrade.service;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

/** Consulta de solo lectura de las tablas de la base de datos para el panel de administracion. */
@Service
public class AdminService {

    // Solo se pueden consultar estas tablas (lista cerrada: el nombre nunca viene "a pelo" del cliente)
    private static final List<String> TABLAS = List.of(
            "Usuario", "Direccion", "Carrito", "Producto", "DetalleCarrito",
            "Pedido", "DetallePedido", "Pago", "Favorito", "Incidencia");

    // Columnas que nunca se envian al navegador
    private static final Set<String> COLUMNAS_OCULTAS = Set.of("password");

    private static final int LIMITE_MAXIMO = 500;

    private final JdbcClient jdbc;

    public AdminService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** nombre en minusculas -> nombre real en la base (en Windows MySQL/MariaDB suele ser minuscula) */
    private Map<String, String> tablasExistentes() {
        List<String> reales = jdbc.sql("""
                SELECT table_name
                FROM information_schema.tables
                WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'
                """)
                .query(String.class)
                .list();

        Map<String, String> mapa = new HashMap<>();
        for (String real : reales) {
            mapa.put(real.toLowerCase(Locale.ROOT), real);
        }
        return mapa;
    }

    public List<Map<String, Object>> tablas() {
        Map<String, String> existentes = tablasExistentes();
        List<Map<String, Object>> salida = new ArrayList<>();

        for (String tabla : TABLAS) {
            String real = existentes.get(tabla.toLowerCase(Locale.ROOT));
            if (real == null) {
                continue;
            }
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("nombre", tabla);
            fila.put("filas", contar(real));
            salida.add(fila);
        }
        return salida;
    }

    public Map<String, Object> contenido(String nombre, int limite) {

        String tabla = TABLAS.stream()
                .filter(t -> t.equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Tabla no permitida o inexistente"));

        String real = tablasExistentes().get(tabla.toLowerCase(Locale.ROOT));
        if (real == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "La tabla " + tabla + " no existe en la base de datos");
        }

        int tope = Math.max(1, Math.min(limite, LIMITE_MAXIMO));

        List<String> columnas = jdbc.sql("""
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = DATABASE() AND table_name = ?
                ORDER BY ordinal_position
                """)
                .param(real)
                .query(String.class)
                .list();

        // "real" viene de information_schema y ademas coincide con la lista cerrada: es seguro usarlo
        List<Map<String, Object>> filas = new ArrayList<>();
        for (Map<String, Object> fila : jdbc.sql("SELECT * FROM `" + real + "` ORDER BY 1 LIMIT " + tope)
                .query()
                .listOfRows()) {
            Map<String, Object> copia = new LinkedHashMap<>(fila);
            for (String oculta : COLUMNAS_OCULTAS) {
                for (String clave : copia.keySet()) {
                    if (clave.equalsIgnoreCase(oculta)) {
                        copia.put(clave, "********");
                    }
                }
            }
            filas.add(copia);
        }

        Map<String, Object> salida = new LinkedHashMap<>();
        salida.put("nombre", tabla);
        salida.put("columnas", columnas);
        salida.put("total", contar(real));
        salida.put("limite", tope);
        salida.put("filas", filas);
        return salida;
    }

    private long contar(String real) {
        Long n = jdbc.sql("SELECT COUNT(*) FROM `" + real + "`").query(Long.class).single();
        return n == null ? 0 : n;
    }
}
