# CardTradeDB sin Docker

Backend Java 17 + Spring Boot conectado a un MySQL instalado directamente en Windows.

## Requisitos

- Java 17 o posterior
- Maven 3.9 o posterior
- MySQL Server 8, recomendado como servicio `MySQL80`
- Opcional: MySQL Workbench

## Preparación rápida

1. Instala MySQL Server y configura el usuario `root`.
2. Si tu contraseña no es `root`, cambia `DB_PASSWORD` en `iniciar_sin_docker.bat` y `ejecutar_jar.bat`.
3. Ejecuta `instalar_base_datos.bat` una sola vez.
4. Ejecuta `iniciar_sin_docker.bat`.
5. Abre `http://localhost:8080`.

## Si el servicio no se llama MySQL80

Ejecuta:

```bat
sc query type= service state= all | findstr /I mysql
```

Cambia `MYSQL_SERVICE=MySQL80` en `iniciar_sin_docker.bat` por el nombre que aparezca.

## Endpoints

- `GET /api/usuarios`
- `POST /api/usuarios`
- `GET /api/productos`
- `POST /api/productos`
- `GET /api/exportacion/inserts`

### Crear usuario

```json
{"usuario":"nuevoUsuario","email":"nuevo@example.com","telefono":"600000004","password":"password123","rol":"CLIENTE"}
```

### Crear producto

```json
{"cartaExternaId":"base1-4","idioma":"es","estadoCarta":"EXCELENTE","acabado":"HOLO","precio":120.50,"stock":2,"activo":true}
```

## Compilar

Ejecuta `compilar.bat`. Después puedes iniciar con `ejecutar_jar.bat`.

## Importante

- MySQL es la fuente principal de datos.
- `inserts.sql` es una carga inicial.
- La exportación generada puede contener hashes y debe tratarse como información sensible.
- El frontend adjunto sigue consultando su API externa; la conexión de ese frontend con estos endpoints es el siguiente paso.
