package com.cardtrade.controller;

import com.cardtrade.service.SesionService;
import com.cardtrade.service.SesionService.Sesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ServletRequestPathUtils;

/**
 * Se ejecuta antes de cualquier peticion a /api/**.
 *  - Si llega un token valido, deja la sesion en el atributo "sesionUsuario".
 *  - Segun la ruta exige: nada (PUBLICO), estar identificado (LOGIN) o ser administrador (ADMIN).
 * Responde 401 (sin sesion valida) o 403 (sin permisos) con el mismo JSON que el resto de la API.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO = "sesionUsuario";

    enum Nivel { PUBLICO, LOGIN, ADMIN }

    private final SesionService sesiones;

    public AuthInterceptor(SesionService sesiones) {
        this.sesiones = sesiones;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {

        // Las peticiones OPTIONS (CORS) no llevan Authorization: se dejan pasar
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            return true;
        }

        String ruta = ServletRequestPathUtils.getParsedRequestPath(req).pathWithinApplication().value();

        Optional<Sesion> sesion = sesiones.obtener(extraerToken(req.getHeader("Authorization")));
        sesion.ifPresent(s -> req.setAttribute(ATRIBUTO, s));

        Nivel nivel = nivelRequerido(req.getMethod(), ruta);

        if (nivel == Nivel.PUBLICO) {
            return true;
        }
        if (sesion.isEmpty()) {
            return rechazar(res, 401, "Debes iniciar sesion");
        }
        if (nivel == Nivel.ADMIN && !sesion.get().esAdmin()) {
            return rechazar(res, 403, "Se necesita ser administrador");
        }

        // Un cliente solo puede tocar sus propios datos (carrito, favoritos, pedidos)
        Long idRuta = idUsuarioEnRuta(ruta);
        if (idRuta != null && !sesion.get().esAdmin() && idRuta != sesion.get().usuarioId()) {
            return rechazar(res, 403, "No puedes acceder a los datos de otro usuario");
        }
        return true;
    }

    static Nivel nivelRequerido(String metodo, String ruta) {
        if (coincide(ruta, "/api/admin") || coincide(ruta, "/api/exportacion")) {
            return Nivel.ADMIN;
        }
        if (ruta.equals("/api/usuarios") && "GET".equalsIgnoreCase(metodo)) {
            return Nivel.ADMIN;
        }
        if (ruta.equals("/api/productos") && "POST".equalsIgnoreCase(metodo)) {
            return Nivel.ADMIN;
        }
        if (coincide(ruta, "/api/carrito") || coincide(ruta, "/api/favoritos") || coincide(ruta, "/api/pedidos")) {
            return Nivel.LOGIN;
        }
        return Nivel.PUBLICO;
    }

    private static boolean coincide(String ruta, String base) {
        return ruta.equals(base) || ruta.startsWith(base + "/");
    }

    /** /api/carrito/{id}..., /api/favoritos/{id} y /api/pedidos/usuario/{id} */
    static Long idUsuarioEnRuta(String ruta) {
        String[] p = ruta.split("/");   // ["", "api", "carrito", "5", ...]
        String texto = null;
        if (p.length > 3 && (p[2].equals("carrito") || p[2].equals("favoritos"))) {
            texto = p[3];
        } else if (p.length > 4 && p[2].equals("pedidos") && p[3].equals("usuario")) {
            texto = p[4];
        }
        if (texto == null) {
            return null;
        }
        try {
            return Long.parseLong(texto);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String extraerToken(String cabecera) {
        if (cabecera == null) {
            return null;
        }
        String c = cabecera.trim();
        if (c.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return c.substring(7).trim();
        }
        return null;
    }

    private boolean rechazar(HttpServletResponse res, int codigo, String mensaje) throws IOException {
        res.setStatus(codigo);
        res.setContentType("application/json");
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.getWriter().write("{\"correcto\":false,\"mensaje\":\"" + mensaje + "\"}");
        return false;
    }
}
