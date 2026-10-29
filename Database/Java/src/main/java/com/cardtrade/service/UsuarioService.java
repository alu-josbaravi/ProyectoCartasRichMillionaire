package com.cardtrade.service;

import com.cardtrade.dto.UsuarioRequest;
import java.util.*;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final JdbcClient jdbc;
    private final BCryptPasswordEncoder encoder;

    public UsuarioService(JdbcClient jdbc, BCryptPasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @Transactional
    public long crear(UsuarioRequest r) {

        String rol = (r.rol() == null || r.rol().isBlank())
                ? "CLIENTE"
                : r.rol();

        jdbc.sql("""
            INSERT INTO Usuario
            (usuario, email, telefono, password, rol)
            VALUES (?, ?, ?, ?, ?)
        """)
                .params(
                        r.usuario(),
                        r.email(),
                        blankToNull(r.telefono()),
                        encoder.encode(r.password()),
                        rol
                )
                .update();

        Long id = jdbc.sql("SELECT LAST_INSERT_ID()")
                .query(Long.class)
                .single();

        jdbc.sql("INSERT INTO Carrito(usuario_id) VALUES(?)")
                .param(id)
                .update();

        return id;
    }

    public List<Map<String, Object>> listar() {
        return jdbc.sql("""
                SELECT id,
                       usuario,
                       email,
                       telefono,
                       fecha_registro,
                       rol
                FROM Usuario
                ORDER BY id
                """)
                .query()
                .listOfRows();
    }

    private String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v;
    }
}