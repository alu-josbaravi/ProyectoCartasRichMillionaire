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
    private final SesionService sesiones;

    public AuthService(JdbcClient jdbc, BCryptPasswordEncoder encoder, SesionService sesiones) {
        this.jdbc = jdbc;
        this.encoder = encoder;
        this.sesiones = sesiones;
    }

    public Map<String, Object> login(LoginRequest r) {

        String login = r.usuario().trim();

        Map<String, Object> u = jdbc.sql("""
                SELECT id, usuario, email, rol, password
                FROM Usuario
                WHERE usuario = ? OR email = ?
                """)
                .params(login, login)
                .query()
                .listOfRows()
                .stream()
                .findFirst()
                .orElse(null);

        // Mismo mensaje en ambos casos para no revelar si el usuario existe
        if (u == null || !encoder.matches(r.password(), (String) u.get("password"))) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos");
        }

        long id = ((Number) u.get("id")).longValue();
        String usuario = (String) u.get("usuario");
        String rol = u.get("rol").toString();

        Map<String, Object> salida = new LinkedHashMap<>();
        salida.put("id", id);
        salida.put("usuario", usuario);
        salida.put("email", u.get("email"));
        salida.put("rol", rol);
        salida.put("token", sesiones.crear(id, usuario, rol));
        return salida;
    }
}
