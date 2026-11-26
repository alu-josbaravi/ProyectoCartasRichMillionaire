package com.cardtrade.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * Sesiones en memoria. El login devuelve un token aleatorio que el frontend envia en
 * "Authorization: Bearer <token>". Si se reinicia el servidor las sesiones se pierden
 * y hay que iniciar sesion otra vez (suficiente para desarrollo y para el proyecto).
 */
@Service
public class SesionService {

    public static final String ROL_ADMIN = "ADMINISTRADOR";
    private static final Duration DURACION = Duration.ofHours(8);

    public record Sesion(long usuarioId, String usuario, String rol, Instant expira) {
        public boolean esAdmin() {
            return ROL_ADMIN.equals(rol);
        }
    }

    private final SecureRandom random = new SecureRandom();
    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();

    public String crear(long usuarioId, String usuario, String rol) {
        limpiarCaducadas();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sesiones.put(token, new Sesion(usuarioId, usuario, rol, Instant.now().plus(DURACION)));
        return token;
    }

    public Optional<Sesion> obtener(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        Sesion s = sesiones.get(token);
        if (s == null) {
            return Optional.empty();
        }
        if (s.expira().isBefore(Instant.now())) {
            sesiones.remove(token);
            return Optional.empty();
        }
        return Optional.of(s);
    }

    public void cerrar(String token) {
        if (token != null) {
            sesiones.remove(token);
        }
    }

    private void limpiarCaducadas() {
        Instant ahora = Instant.now();
        sesiones.values().removeIf(s -> s.expira().isBefore(ahora));
    }
}
