package com.cardtrade.service;

import com.cardtrade.dto.ProductoRequest;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final JdbcClient jdbc;

    public ProductoService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public long crear(ProductoRequest r) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.sql("""
            INSERT INTO Producto
            (carta_externa_id, idioma, estado_carta, acabado, precio, stock, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """)
                .params(
                        r.cartaExternaId(),
                        r.idioma(),
                        r.estadoCarta(),
                        r.acabado(),
                        r.precio(),
                        r.stock(),
                        r.activo() == null || r.activo()
                )
                .update(keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<Map<String, Object>> listar() {
        return jdbc.sql("SELECT * FROM Producto ORDER BY id")
                .query()
                .listOfRows();
    }

    /** Solo productos a la venta (activos y con stock): lo que ve el cliente. */
    public List<Map<String, Object>> disponibles() {
        return jdbc.sql("""
                SELECT id, carta_externa_id, idioma, estado_carta, acabado, precio, stock
                FROM Producto
                WHERE activo = TRUE AND stock > 0
                ORDER BY carta_externa_id, precio
                """)
                .query()
                .listOfRows();
    }
}
