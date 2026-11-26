# CAMBIOS de esta reparación

## Causas reales de lo que fallaba
1. **No se había copiado ninguna clase Java nueva**: el ZIP anterior traía `tienda.js/css` pero ninguno de los controladores/servicios, y el JAR de `target/` era el antiguo (solo `index.html` y `Index.css`). Por eso salía la versión sin tienda.
2. **`foc / foc`, `AdminInicial`, `AdminController`, `/api/admin/tablas` y `admin.html` no existían en el proyecto**: no estaban en este ZIP. Ahora existen (ver abajo).
3. **Hash de `inserts.sql` inválido**: los 3 usuarios de prueba tenían un hash BCrypt de 59 caracteres (el válido tiene 60), así que ninguno podía entrar. Corregido.
4. Mi `PedidoService` anterior usaba columnas que no existen (`calle`, `numero`...); el esquema real usa `calle_envio`, `numero_envio`... Corregido y validado contra el esquema.
5. `estado_carta`: el código aceptaba `ACEPTABLE/DANADA` y la base `JUGADA/DETERIORADA`. Alineado con la base.
6. `Usuario.usuario` es `VARCHAR(16)` y el DTO permitía 50. Alineado.
7. `instalar_base_datos.bat` usaba los SQL antiguos de `Database/Java/database/` (esquema incompleto). Ahora usa `Database/*.sql`.

## Archivos
**Nuevos:** `Frontend/admin.html`; `Database/Java/`: `detener.bat`, `probar_api.bat`, `pruebas/ProbarApi.java`; Java (`com.cardtrade`): `config/AdminInicial`, `config/WebConfig`, `controller/{AuthInterceptor,AdminController,CarritoController,FavoritoController,PedidoController}`, `service/{ApiException,SesionService,AdminService,CarritoService,FavoritoService,PedidoService}`, `dto/{LoginRequest,CarritoItemRequest,CantidadRequest,FavoritoRequest,PedidoRequest}`.
**Modificados:** `Frontend/{index.html,tienda.js,tienda.css}`, `Database/inserts.sql` (hashes), `Database/Java/{pom.xml,iniciar.bat,instalar_base_datos.bat,README_JAVA.md}`, `application.properties` (solo se añade el bloque `app.admin-inicial.*`), `AuthController`, `UsuarioController`, `ProductoController`, `ProductoService`, `GlobalExceptionHandler` (404/405/400 reales), `ProductoRequest`, `UsuarioRequest`, `README.md`, `target/` (JAR y clases regenerados).
**Eliminados (duplicados):** `Database/Java/src/main/resources/static/` y `Database/Java/database/`. Si extraes encima de tu carpeta, borra a mano esas dos si siguen ahí (aunque queden, el `pom.xml` las ignora).

## Verificado y NO verificado
Verificado aquí: compilación de las 35 clases sin errores; arranque del JAR; 31 sentencias SQL contra el esquema real; BCrypt; seguridad 401/403/404/CORS/logout con sesiones simuladas; archivos estáticos servidos desde el JAR.
**No verificado** (sin MariaDB/MySQL ni Windows aquí): login `foc/foc` contra la base real, creación de `foc` en `AdminInicial`, consultas de `/api/admin/tablas`, carrito/pedidos con la base real, los `.bat`, y `mvn clean test package` (el JAR se montó a mano con `javac` sobre el JAR anterior; no hay tests JUnit). Para eso: `instalar_base_datos.bat`, `detener.bat`, `iniciar.bat` y `probar_api.bat`.
