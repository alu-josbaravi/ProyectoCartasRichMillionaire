# CardTrade Backend Java

<<<<<<< Updated upstream
Backend de ejemplo para conectar un frontend con la base de datos **CardTradeDB**. La aplicación valida los datos, los inserta en MySQL, informa al frontend del resultado y permite descargar un `inserts.sql` generado con los datos actuales.

## Funciones incluidas

- Alta y listado de usuarios.
- Cifrado de contraseñas con BCrypt.
- Creación automática del carrito al registrar un usuario.
- Alta y listado de productos.
- Validación del JSON recibido en Java.
- Control de duplicados y restricciones de MySQL.
- Frontend de prueba incluido en la propia aplicación.
- Exportación completa de las diez tablas a `inserts.sql`.

## Requisitos

- Java 17 o posterior.
- Maven 3.9 o posterior.
- MySQL 8.
- Opcionalmente Docker para arrancar MySQL rápidamente.

## Estructura

- `src/main/java`: API, servicios, validaciones y exportador SQL.
- `src/main/resources/static`: frontend de prueba.
- `src/main/resources/application.properties`: conexión con MySQL.
- `database`: esquema y datos iniciales.
- `docker-compose.yml`: MySQL preparado para pruebas.


## Inicio automático en Windows

Para ejecutar el proyecto con el mínimo número de pasos:

1. Instala Java 17 o posterior, Maven 3.9 o posterior y Docker Desktop.
2. Inicia Docker Desktop.
3. Haz doble clic en `iniciar.bat`.
4. El archivo comprobará los programas necesarios, iniciará MySQL, esperará a que esté disponible, configurará la conexión, arrancará Spring Boot y abrirá `http://localhost:8080`.
5. Mantén abierta la ventana mientras uses la aplicación. Para detener Java, pulsa `Ctrl+C`.

Archivos auxiliares:

- `config.ini`: muestra la configuración principal del proyecto. Es informativo; los valores de ejecución también están preparados en los archivos BAT y en `docker-compose.yml`.
- `iniciar.bat`: inicia MySQL y la aplicación Java.
- `detener.bat`: detiene el contenedor de MySQL sin borrar sus datos.
- `compilar.bat`: genera el archivo JAR mediante Maven.
- `reiniciar_base_datos.bat`: elimina todos los datos y vuelve a cargar los SQL iniciales. Pide escribir `REINICIAR` para evitar borrados accidentales.

> Un archivo `.ini` solo guarda configuración y no puede ejecutar órdenes por sí mismo. Por eso se incluye `config.ini` junto con archivos `.bat`, que son los que automatizan el proceso en Windows.

## Puesta en marcha rápida

### 1. Arrancar MySQL con Docker

Desde la carpeta del proyecto:

```bash
docker compose up -d
```

La primera vez se ejecutarán automáticamente `database/CardTradeDB.sql` y `database/inserts.sql`.

Si no se usa Docker, hay que ejecutar manualmente ambos archivos en este orden:

```bash
mysql -u root -p < database/CardTradeDB.sql
mysql -u root -p tienda_cartas < database/inserts.sql
```

### 2. Configurar la conexión

Los valores predeterminados son:

```properties
DB_URL=jdbc:mysql://localhost:3306/tienda_cartas
DB_USER=root
DB_PASSWORD=root
```

Se pueden modificar mediante variables de entorno. Ejemplo en Linux o macOS:

```bash
export DB_USER=root
export DB_PASSWORD=mi_clave
```

En PowerShell:

```powershell
$env:DB_USER="root"
$env:DB_PASSWORD="mi_clave"
```

### 3. Ejecutar Java

```bash
mvn spring-boot:run
```

Después, abrir:

```text
http://localhost:8080
```

## Cómo funciona

1. El formulario convierte los datos en JSON con JavaScript.
2. `fetch()` envía el JSON a un endpoint bajo `/api`.
3. Java valida campos obligatorios, tamaños, correo, precio y stock.
4. El servicio ejecuta un `INSERT` preparado mediante `JdbcClient`.
5. MySQL comprueba claves únicas, foráneas y restricciones `CHECK`.
6. La API devuelve JSON con `correcto`, `mensaje` y `datos`.
7. El frontend muestra esa respuesta en pantalla.

La inserción de un usuario y la creación de su carrito se realizan en la misma transacción. Si una de las operaciones falla, no se guarda ninguna.
=======
CardTradeDB es el backend de una tienda de cartas coleccionables. Está desarrollado con Java y Spring Boot y se conecta a una base de datos MariaDB instalada directamente en Windows.

La aplicación permite gestionar usuarios y productos mediante una API REST, aplicar validaciones, almacenar contraseñas mediante BCrypt, crear automáticamente un carrito al registrar un usuario y exportar los datos existentes a un archivo `inserts.sql`.

El proyecto también incluye un frontend de búsqueda de cartas. Actualmente, el buscador consulta una API externa para obtener la información general de las cartas. La conexión completa entre el frontend y los endpoints propios de CardTradeDB es el siguiente paso del desarrollo.

---

## Tecnologías utilizadas

- Java 17 o posterior. Java 21 recomendado.
- Spring Boot 3.5.7.
- MariaDB 11.
- Spring JDBC y `JdbcClient`.
- BCrypt para proteger las contraseñas.
- HTML, CSS y JavaScript.
- IntelliJ IDEA.
- Maven integrado en IntelliJ IDEA.

El proyecto no necesita Docker para ejecutarse.

---

## Estructura principal

```text
Database/Java/
├── database/
│   ├── CardTradeDB.sql
│   └── inserts.sql
├── src/
│   └── main/
│       ├── java/
│       │   └── com/cardtrade/
│       │       ├── config/
│       │       ├── controller/
│       │       ├── dto/
│       │       └── service/
│       └── resources/
│           ├── application.properties
│           └── static/
│               ├── index.html
│               ├── Index.css
│               └── favicon.ico
├── target/
│   └── cardtrade-backend-1.0.0.jar
├── pom.xml
├── iniciar.bat
├── instalar_base_datos.bat
├── abrir_intellij.bat
└── recompilar.bat
```

El archivo `favicon.ico` es opcional. Si no existe, el navegador puede solicitarlo y generar un aviso en la consola, pero esto no impide que la aplicación funcione.

---

## Requisitos

### Para ejecutar el proyecto

- Java 17 o posterior.
- MariaDB Server.
- La base de datos `tienda_cartas`.
- El archivo compilado `target/cardtrade-backend-1.0.0.jar`.

### Para modificar y recompilar el backend

- IntelliJ IDEA.
- El proyecto importado como proyecto Maven.

No es necesario instalar Maven globalmente en Windows. IntelliJ IDEA utiliza su propia instalación integrada de Maven.

---

## Configuración de MariaDB

El proyecto está configurado para conectarse a:

```text
Servidor: localhost
Puerto: 3306
Base de datos: tienda_cartas
Usuario: root
```

La conexión se configura en:
>>>>>>> Stashed changes

```text
src/main/resources/application.properties
```

<<<<<<< Updated upstream
### Crear usuario

`POST /api/usuarios`
=======
Configuración utilizada:

```properties
spring.application.name=cardtrade-backend

server.port=8080

spring.datasource.url=jdbc:mariadb://localhost:3306/tienda_cartas
spring.datasource.username=root
spring.datasource.password=CONTRASENA_DE_MARIADB
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

spring.sql.init.mode=never

spring.jackson.default-property-inclusion=non_null
```

Sustituye `CONTRASENA_DE_MARIADB` por la contraseña real del usuario `root`.

> No se recomienda subir una contraseña real a un repositorio público. Para compartir el proyecto, utiliza un valor de ejemplo o variables de entorno.

---

## Dependencia de MariaDB

El archivo `pom.xml` debe incluir el conector oficial de MariaDB:

```xml
<dependency>
    <groupId>org.mariadb.jdbc</groupId>
    <artifactId>mariadb-java-client</artifactId>
    <scope>runtime</scope>
</dependency>
```

No debe utilizarse simultáneamente el conector de MySQL si la conexión está configurada mediante `jdbc:mariadb://`.

---

## Preparación inicial de la base de datos

Los archivos SQL se encuentran en:

```text
database/CardTradeDB.sql
database/inserts.sql
```

Deben ejecutarse en este orden:

1. `CardTradeDB.sql`.
2. `inserts.sql`.

Pueden ejecutarse desde DataGrip, el cliente de MariaDB o mediante:

```text
instalar_base_datos.bat
```

`CardTradeDB.sql` crea la base de datos y las tablas. `inserts.sql` introduce los datos iniciales de prueba.

> Aviso: `CardTradeDB.sql` puede eliminar y volver a crear la base de datos. No debe ejecutarse sobre una base que contenga información que se quiera conservar.

---

## Ejecución normal

Si el JAR ya está compilado, no hace falta abrir IntelliJ ni tener Maven instalado.

Desde la carpeta `Database/Java`, ejecuta:

```bat
java -jar target\cardtrade-backend-1.0.0.jar
```

También se puede hacer doble clic en:

```text
iniciar.bat
```

Cuando la aplicación arranque correctamente, la consola mostrará mensajes parecidos a:

```text
Tomcat started on port 8080
Started CardTradeApplication
HikariPool-1 - Added connection org.mariadb.jdbc.Connection
```

La aplicación estará disponible en:

```text
http://localhost:8080
```

Para detenerla, pulsa `Ctrl+C` en la consola donde se está ejecutando.

---

## Recompilar después de modificar el proyecto

Si se modifica código Java, HTML, CSS, `pom.xml` o `application.properties`, es necesario volver a generar el JAR.

Antes de recompilar, detén la aplicación con:

```text
Ctrl+C
```

No se puede recompilar mientras el JAR está ejecutándose porque Windows mantiene el archivo bloqueado.

Después:

1. Abre el proyecto en IntelliJ IDEA.
2. Abre el panel `Maven` de la derecha.
3. Expande `CardTrade Backend`.
4. Expande `Lifecycle`.
5. Haz doble clic en `package`.
6. Espera a que aparezca `BUILD SUCCESS`.

El nuevo archivo se generará en:

```text
target/cardtrade-backend-1.0.0.jar
```

También pueden utilizarse `abrir_intellij.bat` y `recompilar.bat`.

---

## Endpoints disponibles

### Listar usuarios

```http
GET /api/usuarios
```

Ejemplo:

```text
http://localhost:8080/api/usuarios
```

Respuesta:

```json
{
  "correcto": true,
  "mensaje": "Usuarios obtenidos",
  "datos": [
    {
      "id": 1,
      "usuario": "admin",
      "email": "admin@tiendacartas.es",
      "telefono": "600000001",
      "fecha_registro": "2026-10-01T08:00:00",
      "rol": "ADMINISTRADOR"
    }
  ]
}
```

Los listados de usuarios no devuelven los hashes de las contraseñas.

### Crear usuario

```http
POST /api/usuarios
```

Cuerpo JSON:
>>>>>>> Stashed changes

```json
{
  "usuario": "nuevoUsuario",
  "email": "nuevo@example.com",
  "telefono": "600000004",
  "password": "password123",
  "rol": "CLIENTE"
}
<<<<<<< Updated upstream
=======
```

Respuesta esperada:

```json
{
  "correcto": true,
  "mensaje": "Usuario insertado correctamente",
  "datos": {
    "id": 4
  }
}
```

La creación del usuario y de su carrito se realiza dentro de una misma transacción. Si falla una de las operaciones, no se guarda ninguna.

### Listar productos

```http
GET /api/productos
```

Ejemplo:

```text
http://localhost:8080/api/productos
>>>>>>> Stashed changes
```

### Listar usuarios

`GET /api/usuarios`

La respuesta no incluye hashes de contraseña.

### Crear producto

<<<<<<< Updated upstream
`POST /api/productos`

```json
{
  "cartaExternaId": "base1-4",
  "idioma": "es",
  "estadoCarta": "EXCELENTE",
  "acabado": "HOLO",
  "precio": 120.50,
  "stock": 2,
  "activo": true
}
```

### Listar productos

`GET /api/productos`

### Descargar los datos actuales

`GET /api/exportacion/inserts`

También se puede pulsar el botón **Descargar inserts.sql actual** del frontend. El exportador consulta todas las tablas y escribe los `INSERT` respetando el orden de las claves foráneas.

## Respuestas

Éxito:
=======
```http
POST /api/productos
```

Cuerpo JSON:

```json
{
  "cartaExternaId": "base1-4",
  "idioma": "es",
  "estadoCarta": "EXCELENTE",
  "acabado": "HOLO",
  "precio": 120.50,
  "stock": 2,
  "activo": true
}
```

Respuesta esperada:

```json
{
  "correcto": true,
  "mensaje": "Producto insertado correctamente",
  "datos": {
    "id": 3
  }
}
```

La combinación de carta externa, idioma, estado y acabado debe ser única.

### Exportar los datos

```http
GET /api/exportacion/inserts
```

Ejemplo:

```text
http://localhost:8080/api/exportacion/inserts
```

Este endpoint genera un archivo `inserts.sql` con los datos almacenados actualmente en las tablas.

> El archivo exportado puede incluir hashes de contraseñas y debe tratarse como información sensible.

---

# Conexión del frontend con el backend

## Situación actual

El frontend realiza búsquedas de cartas mediante una API externa. Esta parte puede seguir utilizándose para obtener datos generales como:

- Nombre de la carta.
- Colección.
- Número.
- Rareza.
- Tipo.
- Puntos de vida.
- Artista.
- Imagen.

El backend propio debe utilizarse para los datos comerciales y operativos:

- Usuarios.
- Productos.
- Precio.
- Stock.
- Estado de conservación.
- Acabado.
- Carritos.
- Pedidos.
- Pagos.
- Favoritos.
- Incidencias.

---

## Conexión desde el mismo ordenador

Si frontend y backend se abren desde el mismo equipo, el frontend puede utilizar:

```javascript
const API_BASE_URL = "http://localhost:8080/api";
```

### Consultar usuarios

```javascript
async function obtenerUsuarios() {
    const respuesta = await fetch(`${API_BASE_URL}/usuarios`);

    if (!respuesta.ok) {
        throw new Error(`Error HTTP: ${respuesta.status}`);
    }

    return await respuesta.json();
}
```

### Consultar productos

```javascript
async function obtenerProductos() {
    const respuesta = await fetch(`${API_BASE_URL}/productos`);

    if (!respuesta.ok) {
        throw new Error(`Error HTTP: ${respuesta.status}`);
    }

    return await respuesta.json();
}
```

### Crear usuario

```javascript
async function crearUsuario(usuario) {
    const respuesta = await fetch(`${API_BASE_URL}/usuarios`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(usuario)
    });

    const resultado = await respuesta.json();

    if (!respuesta.ok || !resultado.correcto) {
        throw new Error(resultado.mensaje || "No se pudo crear el usuario");
    }

    return resultado;
}
```

Ejemplo:

```javascript
crearUsuario({
    usuario: "antonio",
    email: "antonio@example.com",
    telefono: "600000005",
    password: "password123",
    rol: "CLIENTE"
})
.then(resultado => console.log(resultado))
.catch(error => console.error(error));
```

### Crear producto

```javascript
async function crearProducto(producto) {
    const respuesta = await fetch(`${API_BASE_URL}/productos`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(producto)
    });

    const resultado = await respuesta.json();

    if (!respuesta.ok || !resultado.correcto) {
        throw new Error(resultado.mensaje || "No se pudo crear el producto");
    }

    return resultado;
}
```

---

## Conexión desde otro ordenador de la misma red

`localhost` siempre representa el ordenador donde se ejecuta el navegador.

Si el frontend se abre desde otro ordenador, no debe utilizar:

```text
http://localhost:8080
```

Hay que obtener la dirección IP del ordenador que ejecuta el backend. En CMD:

```bat
ipconfig
```

Busca la `Dirección IPv4`. Por ejemplo:

```text
192.168.1.35
```

Entonces el frontend deberá utilizar:

```javascript
const API_BASE_URL = "http://192.168.1.35:8080/api";
```

Los dos equipos deben estar conectados a la misma red. También puede ser necesario permitir Java o el puerto `8080` en el Firewall de Windows.

Para comprobar la conexión desde el otro ordenador, abre:

```text
http://192.168.1.35:8080/api/usuarios
```

Si aparece el JSON de usuarios, la comunicación funciona.

---

## Flujo recomendado de integración

```text
Frontend
   |
   | fetch() con JSON
   v
Backend Spring Boot
   |
   | validaciones y consultas preparadas
   v
MariaDB
   |
   | resultado de la operación
   v
Backend
   |
   | respuesta JSON
   v
Frontend
```

Ejemplo de respuesta correcta:
>>>>>>> Stashed changes

```json
{
  "correcto": true,
  "mensaje": "Producto insertado correctamente",
<<<<<<< Updated upstream
  "datos": { "id": 13 }
}
```

Error:
=======
  "datos": {
    "id": 5
  }
}
```

Ejemplo de error:
>>>>>>> Stashed changes

```json
{
  "correcto": false,
<<<<<<< Updated upstream
  "mensaje": "No se pudo insertar: hay un valor duplicado o se incumple una restricción de la base de datos",
=======
  "mensaje": "Valor duplicado o restricción de base de datos incumplida",
>>>>>>> Stashed changes
  "datos": null
}
```

<<<<<<< Updated upstream
## Compilar y ejecutar el JAR

```bash
mvn clean package
java -jar target/cardtrade-backend-1.0.0.jar
```

## Pruebas rápidas con curl

```bash
curl http://localhost:8080/api/productos
curl -OJ http://localhost:8080/api/exportacion/inserts
```

## Seguridad y límites

Este proyecto es una base funcional para desarrollo y clase. Antes de publicarlo se deben añadir autenticación, autorización por roles, HTTPS, límites de peticiones y una política CORS concreta. Las contraseñas se reciben únicamente para registrarlas y se almacenan como hash BCrypt. Nunca se exporta la contraseña original.

El archivo exportado incluye los hashes almacenados porque su objetivo es reproducir la base de datos. Debe tratarse como información sensible y no publicarse.

## Reiniciar la base de datos de Docker

Para eliminar el volumen y volver a cargar los SQL iniciales:

```bash
docker compose down -v
docker compose up -d
```
=======
El frontend debe comprobar tanto el código HTTP como el campo `correcto`.

---

## Integración con la información externa de cartas

El frontend puede continuar consultando la API externa para localizar cartas.

Cuando una carta se añada como producto de la tienda, el frontend debe enviar al backend:

```json
{
  "cartaExternaId": "identificador-de-la-api",
  "idioma": "es",
  "estadoCarta": "EXCELENTE",
  "acabado": "HOLO",
  "precio": 15.50,
  "stock": 3,
  "activo": true
}
```

De este modo:

- La API externa aporta los datos generales.
- MariaDB guarda los datos comerciales.
- `cartaExternaId` relaciona ambas partes.
- No se duplican innecesariamente los datos generales de la carta.

---

## Valores admitidos para productos

### Estado de la carta

```text
NUEVA
EXCELENTE
BUENA
ACEPTABLE
DANADA
```

### Acabado

```text
NORMAL
HOLO
REVERSE_HOLO
```

El frontend debe enviar exactamente estos valores, en mayúsculas y sin tildes.

---

## CORS

Los controladores actuales permiten peticiones desde otros orígenes mediante:

```java
@CrossOrigin(origins = "*")
```

Esto facilita las pruebas durante el desarrollo.

Antes de publicar la aplicación debe sustituirse por el origen concreto del frontend, por ejemplo:

```java
@CrossOrigin(origins = "http://localhost:5500")
```

---

## Pruebas recomendadas

1. `GET /api/usuarios`.
2. `GET /api/productos`.
3. Alta correcta de un usuario.
4. Alta con correo duplicado.
5. Alta correcta de un producto.
6. Producto con precio negativo.
7. Producto duplicado.
8. Descarga de `inserts.sql`.
9. Conexión desde el ordenador del frontend.
10. Respuesta cuando el backend está detenido.

---

## Seguridad

El proyecto es una base funcional para desarrollo y clase. Antes de publicarlo deben añadirse:

- Autenticación.
- Autorización por roles.
- HTTPS.
- Configuración CORS restrictiva.
- Límites de peticiones.
- Gestión segura de credenciales.
- Variables de entorno.
- Registro controlado de errores.

Las contraseñas se almacenan con BCrypt. La contraseña original no se guarda.

No deben publicarse:

- Contraseñas de MariaDB.
- Archivos exportados con datos sensibles.
- Hashes completos.
- Credenciales personales.
- Capturas que muestren contraseñas.

---

## Problemas frecuentes

### El JAR no se puede recompilar

Si aparece:

```text
Unable to rename cardtrade-backend-1.0.0.jar
```

el backend sigue ejecutándose. Detén la aplicación con `Ctrl+C` y vuelve a ejecutar `Maven → Lifecycle → package`.

### No hay conexión con MariaDB

Comprueba:

- MariaDB está iniciado.
- La base `tienda_cartas` existe.
- El puerto es `3306`.
- El usuario y la contraseña son correctos.
- El conector de MariaDB está en `pom.xml`.
- La URL comienza por `jdbc:mariadb://`.

### El frontend aparece sin CSS

Comprueba que `index.html` contiene:

```html
<link rel="stylesheet" href="/Index.css">
```

y que el archivo existe en:

```text
src/main/resources/static/Index.css
```

Después recompila el JAR.

### Aparece un error de `favicon.ico`

El aviso:

```text
No static resource favicon.ico
```

no afecta al funcionamiento. Puede eliminarse añadiendo:

```text
src/main/resources/static/favicon.ico
```

---

## Estado actual del proyecto

Actualmente funcionan:

- Arranque de Spring Boot.
- Servidor web en el puerto `8080`.
- Conexión con MariaDB.
- Listado de usuarios.
- Alta de usuarios.
- Creación automática de carritos.
- Listado de productos.
- Alta de productos.
- Validaciones.
- BCrypt.
- Exportación SQL.
- Frontend servido desde Spring Boot.

El siguiente paso principal es conectar los formularios y las acciones del frontend con los endpoints propios del backend.
>>>>>>> Stashed changes
