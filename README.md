# ProyectoCartasRichMillionaire

Tienda de cartas coleccionables: buscador Pokémon (API externa) + tienda (carrito, favoritos, pedidos) + panel de administración, con backend Java (Spring Boot) y base de datos MariaDB/MySQL.

## Estructura (una sola copia de cada cosa)

| Carpeta | Contenido |
|---|---|
| `Frontend/` | **Única copia del frontend**: `index.html`, `Index.css`, `tienda.js`, `tienda.css`, `admin.html`. El `pom.xml` la incluye en el JAR como `static/`. |
| `Database/` | **Únicos SQL**: `CardTradeDB.sql` (esquema) e `inserts.sql` (datos de prueba). |
| `Database/Java/` | Backend, `pom.xml` y los `.bat`. |

## Puesta en marcha (Windows)

1. Inicia MariaDB/MySQL y revisa la contraseña en `Database/Java/src/main/resources/application.properties`.
2. `Database/Java/instalar_base_datos.bat` (crea `tienda_cartas` y carga los datos; **borra** la base si existía).
3. Si has cambiado código o el frontend: `Database/Java/recompilar.bat` → IntelliJ → Maven → `Lifecycle` → `clean` y `package` (espera `BUILD SUCCESS`).
4. `Database/Java/detener.bat` si hay una versión antigua abierta, y después `Database/Java/iniciar.bat`.
5. Tienda: <http://localhost:8080> · Administración: <http://localhost:8080/admin.html>

## Credenciales de desarrollo

| Usuario | Contraseña | Rol | Origen |
|---|---|---|---|
| `foc` | `foc` | ADMINISTRADOR | Se crea/repara sola al arrancar (`AdminInicial`, configurable en `application.properties`: `app.admin-inicial.*`) |
| `admin`, `alejandro`, `lucia` | `password` | ADMINISTRADOR / CLIENTE / CLIENTE | `inserts.sql` |

Fuera del entorno de desarrollo, cambia la contraseña de `foc` o pon `app.admin-inicial.habilitado=false`.

Comprobar la cuenta en la base de datos:

```sql
SELECT id, usuario, email, rol, LENGTH(password) AS longitud_hash, LEFT(password, 4) AS prefijo
FROM Usuario WHERE usuario = 'foc';
-- Esperado: rol = ADMINISTRADOR, longitud_hash = 60, prefijo = $2a$
SELECT COUNT(*) FROM Carrito c JOIN Usuario u ON u.id = c.usuario_id WHERE u.usuario = 'foc';   -- 1
```

## Seguridad de la API

El login (`POST /api/auth/login`) devuelve un token que se envía en `Authorization: Bearer <token>` (sesiones en memoria, 8 h; se pierden al reiniciar).

| Ruta | Acceso |
|---|---|
| `/api/admin/**`, `/api/exportacion/**`, `GET /api/usuarios`, `POST /api/productos` | Solo ADMINISTRADOR (401 sin sesión, 403 si no lo es) |
| `/api/carrito/**`, `/api/favoritos/**`, `/api/pedidos/**` | Sesión iniciada y solo sobre los datos propios (admin: todos) |
| Resto (`login`, `POST /api/usuarios`, `GET /api/productos`...) | Público. Registrarse como ADMINISTRADOR solo lo puede hacer un administrador. |

## Probar la API

Con la aplicación arrancada: `Database/Java/probar_api.bat` (necesita un JDK 17+). Comprueba login `foc/foc`, login incorrecto, `/api/admin/tablas` sin sesión (401), con administrador (200) y con usuario normal (403), y los permisos de carrito/pedidos.

## Verificar qué JAR se ejecuta

```bat
jar tf Database\Java\target\cardtrade-backend-1.0.0.jar | findstr /i "AdminInicial AdminController admin.html tienda.js"
```
Si no salen las cuatro líneas, el JAR es antiguo: ejecuta `detener.bat`, recompila y vuelve a iniciar.
