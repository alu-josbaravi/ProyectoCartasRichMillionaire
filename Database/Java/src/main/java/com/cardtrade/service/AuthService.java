package com.cardtrade.service;

import com.cardtrade.dto.LoginRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JdbcClient jdbc;
    private final BCryptPasswordEncoder encoder;

    public AuthService(JdbcClient jdbc, BCryptPasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    public Map<String, Object> login(LoginRequest r) {

        Map<String, Object> u = jdbc.sql("""
                SELECT id, usuario, email, rol, password
                FROM Usuario
                WHERE usuario = ? OR email = ?
                """)
                .params(r.usuario().trim(), r.usuario().trim())
                .query()
                .listOfRows()
                .stream()
                .findFirst()
                .orElse(null);

        // Mismo mensaje en ambos casos para no revelar si el usuario existe
        if (u == null || !encoder.matches(r.password(), (String) u.get("password"))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        Map<String, Object> salida = new LinkedHashMap<>();
        salida.put("id", u.get("id"));
        salida.put("usuario", u.get("usuario"));
        salida.put("email", u.get("email"));
        salida.put("rol", u.get("rol"));
        return salida;
    }
}
