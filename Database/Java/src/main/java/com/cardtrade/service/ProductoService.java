package com.cardtrade.service;

import com.cardtrade.dto.ProductoRequest;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final JdbcClient jdbc;

    public ProductoService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public long crear(ProductoRequest r) {

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
                .update();

        return jdbc.sql("SELECT LAST_INSERT_ID()")
                .query(Long.class)
                .single();
    }

    public List<Map<String, Object>> listar() {
        return jdbc.sql("SELECT * FROM Producto ORDER BY id")
                .query()
                .listOfRows();
    }
}