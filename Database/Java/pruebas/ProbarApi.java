import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pruebas de regresion de la API con la aplicacion arrancada (no necesita Maven ni librerias).
 *
 *   java pruebas\ProbarApi.java                      -> http://localhost:8080
 *   java pruebas\ProbarApi.java http://localhost:9090
 *
 * Comprueba: login foc/foc, login incorrecto, /api/admin/tablas sin sesion (401), con administrador (200),
 * con usuario normal (403), ruta inexistente (404), y que nadie pueda hacerse administrador al registrarse.
 * Termina con codigo 1 si falla alguna comprobacion.
 */
public class ProbarApi {

    record Resp(int codigo, String cuerpo) {}

    static HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    static String base;
    static int correctas = 0, fallos = 0, omitidas = 0;

    public static void main(String[] args) throws Exception {
        base = args.length > 0 ? args[0].replaceAll("/+$", "") : "http://localhost:8080";
        System.out.println("Probando " + base + "\n");

        try {
            enviar("GET", "/", null, null);
        } catch (Exception e) {
            System.out.println("[ERROR] No se puede conectar con " + base + ". Arranca antes la aplicacion (iniciar.bat).");
            System.exit(2);
        }

        // ---------- Sin sesion (no necesitan datos en la base) ----------
        Resp r = enviar("GET", "/admin.html", null, null);
        comprobar("GET /admin.html -> 200 y es una pagina HTML", r.codigo == 200 && r.cuerpo.contains("<html"), r);

        r = enviar("GET", "/api/admin/tablas", null, null);
        comprobar("GET /api/admin/tablas sin sesion -> 401 con JSON", r.codigo == 401
                && r.cuerpo.contains("\"correcto\":false") && !r.cuerpo.contains("No static resource"), r);

        r = enviar("GET", "/api/admin/tablas", "token-inventado", null);
        comprobar("GET /api/admin/tablas con token falso -> 401", r.codigo == 401, r);

        r = enviar("GET", "/api/admin/lo-que-sea", null, null);
        comprobar("GET /api/admin/lo-que-sea sin sesion -> 401 (nunca 404/500)", r.codigo == 401, r);

        r = enviar("GET", "/api/ruta-que-no-existe", null, null);
        comprobar("GET /api/ruta-que-no-existe -> 404 y no 'Error interno'", r.codigo == 404
                && !r.cuerpo.contains("Error interno"), r);

        r = enviar("GET", "/api/exportacion/inserts", null, null);
        comprobar("GET /api/exportacion/inserts sin sesion -> 401 (no se descarga la base)", r.codigo == 401, r);

        r = enviar("GET", "/api/usuarios", null, null);
        comprobar("GET /api/usuarios sin sesion -> 401", r.codigo == 401, r);

        r = enviar("POST", "/api/productos", null, "{}");
        comprobar("POST /api/productos sin sesion -> 401", r.codigo == 401, r);

        r = enviar("GET", "/api/carrito/1", null, null);
        comprobar("GET /api/carrito/1 sin sesion -> 401", r.codigo == 401, r);

        r = enviar("POST", "/api/usuarios", null,
                "{\"usuario\":\"intruso\",\"email\":\"intruso@x.com\",\"password\":\"12345678\",\"rol\":\"ADMINISTRADOR\"}");
        comprobar("Registrarse como ADMINISTRADOR sin ser admin -> 403", r.codigo == 403, r);

        // ---------- Con la base de datos ----------
        r = enviar("POST", "/api/auth/login", null, "{\"usuario\":\"foc\",\"password\":\"foc\"}");
        boolean loginOk = r.codigo == 200 && r.cuerpo.contains("\"token\"") && r.cuerpo.contains("ADMINISTRADOR");
        comprobar("Login foc / foc -> 200, token y rol ADMINISTRADOR", loginOk, r);
        String tokenAdmin = extraer(r.cuerpo, "\"token\":\"([^\"]+)\"");
        String idAdmin = extraer(r.cuerpo, "\"id\":(\\d+)");

        r = enviar("POST", "/api/auth/login", null, "{\"usuario\":\"foc\",\"password\":\"contrasena-equivocada\"}");
        comprobar("Login con contrasena incorrecta -> 401", r.codigo == 401 && !r.cuerpo.contains("\"token\""), r);

        if (tokenAdmin != null) {
            r = enviar("GET", "/api/admin/tablas", tokenAdmin, null);
            comprobar("GET /api/admin/tablas con foc -> 200 y lista de tablas",
                    r.codigo == 200 && r.cuerpo.contains("\"nombre\":\"Usuario\""), r);

            r = enviar("GET", "/api/admin/tablas/Usuario", tokenAdmin, null);
            comprobar("GET /api/admin/tablas/Usuario con foc -> 200 y sin hashes de contrasena",
                    r.codigo == 200 && r.cuerpo.contains("\"columnas\"") && !r.cuerpo.contains("$2"), r);

            r = enviar("GET", "/api/admin/tablas/TablaQueNoExiste", tokenAdmin, null);
            comprobar("GET /api/admin/tablas/TablaQueNoExiste -> 404", r.codigo == 404, r);
        } else {
            omitir("Consultas administrativas con foc (no se pudo iniciar sesion)");
        }

        // Usuario normal de inserts.sql (contrasena "password")
        r = enviar("POST", "/api/auth/login", null, "{\"usuario\":\"alejandro\",\"password\":\"password\"}");
        String tokenCliente = r.codigo == 200 ? extraer(r.cuerpo, "\"token\":\"([^\"]+)\"") : null;
        String idCliente = r.codigo == 200 ? extraer(r.cuerpo, "\"id\":(\\d+)") : null;

        if (tokenCliente != null) {
            comprobar("Login alejandro / password (usuario normal) -> 200 y rol CLIENTE", r.cuerpo.contains("CLIENTE"), r);

            r = enviar("GET", "/api/admin/tablas", tokenCliente, null);
            comprobar("GET /api/admin/tablas con usuario normal -> 403", r.codigo == 403, r);

            if (idAdmin != null) {
                r = enviar("GET", "/api/carrito/" + idAdmin, tokenCliente, null);
                comprobar("Un cliente no puede ver el carrito de otro usuario -> 403", r.codigo == 403, r);
            }
            if (idCliente != null) {
                r = enviar("GET", "/api/carrito/" + idCliente, tokenCliente, null);
                comprobar("Un cliente puede ver su propio carrito -> 200", r.codigo == 200, r);
            }
        } else {
            omitir("Comprobaciones con usuario normal (alejandro no existe: ejecuta instalar_base_datos.bat)");
        }

        System.out.println("\nResultado: " + correctas + " correctas, " + fallos + " con fallo, " + omitidas + " omitidas.");
        System.exit(fallos == 0 ? 0 : 1);
    }

    static Resp enviar(String metodo, String ruta, String token, String json) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder(URI.create(base + ruta)).timeout(Duration.ofSeconds(20));
        if (token != null) b.header("Authorization", "Bearer " + token);
        if (json != null) b.header("Content-Type", "application/json");
        b.method(metodo, json == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(json));
        HttpResponse<String> res = http.send(b.build(), HttpResponse.BodyHandlers.ofString());
        return new Resp(res.statusCode(), res.body());
    }

    static String extraer(String texto, String regex) {
        Matcher m = Pattern.compile(regex).matcher(texto);
        return m.find() ? m.group(1) : null;
    }

    static void comprobar(String nombre, boolean ok, Resp r) {
        if (ok) {
            correctas++;
            System.out.println("[OK]    " + nombre);
        } else {
            fallos++;
            String cuerpo = r.cuerpo == null ? "" : r.cuerpo.replaceAll("\\s+", " ");
            System.out.println("[FALLO] " + nombre + "  (HTTP " + r.codigo + ": "
                    + (cuerpo.length() > 140 ? cuerpo.substring(0, 140) + "..." : cuerpo) + ")");
        }
    }

    static void omitir(String nombre) {
        omitidas++;
        System.out.println("[OMITIDA] " + nombre);
    }
}
