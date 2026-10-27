# CardTrade Backend Java

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

## Endpoints

### Crear usuario

`POST /api/usuarios`

```json
{
  "usuario": "nuevoUsuario",
  "email": "nuevo@example.com",
  "telefono": "600000004",
  "password": "password123",
  "rol": "CLIENTE"
}
```

### Listar usuarios

`GET /api/usuarios`

La respuesta no incluye hashes de contraseña.

### Crear producto

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

```json
{
  "correcto": true,
  "mensaje": "Producto insertado correctamente",
  "datos": { "id": 13 }
}
```

Error:

```json
{
  "correcto": false,
  "mensaje": "No se pudo insertar: hay un valor duplicado o se incumple una restricción de la base de datos",
  "datos": null
}
```

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
