# CardTradeDB - Tienda de cartas coleccionables

## Descripción

CardTradeDB es la base de datos de una tienda de cartas coleccionables. Permite gestionar usuarios, productos, direcciones de envío, carritos de compra, pedidos, pagos, favoritos e incidencias.

Los datos generales de cada carta, como el nombre, la colección, la rareza o la imagen, no se duplican en la tabla `Producto`. En su lugar, se utiliza `carta_externa_id` para consultar esa información en una fuente externa, como una API o un repositorio de datos.

## Archivos del proyecto

- `CardTradeDB.sql`: elimina y crea la base de datos `tienda_cartas` junto con todas sus tablas, relaciones, índices y restricciones.
- `inserts.sql`: elimina los datos anteriores e introduce un conjunto de datos de prueba respetando las relaciones entre las tablas.
- `README.md`: contiene la documentación general de la base de datos.

## Sistema gestor

El proyecto está preparado para MySQL y utiliza:

- Motor de almacenamiento `InnoDB`.
- Codificación `utf8mb4`.
- Intercalación `utf8mb4_spanish_ci`.
- Claves primarias y foráneas.
- Restricciones `UNIQUE` y `CHECK`.
- Índices para mejorar las consultas más habituales.

## Entidades

### Usuario

Almacena la información de las personas registradas en la plataforma.

**Campos principales:**

- `id`: identificador único.
- `usuario`: nombre de usuario único.
- `email`: correo electrónico único.
- `telefono`: número de teléfono opcional y único.
- `password`: contraseña almacenada de forma cifrada mediante hash.
- `fecha_registro`: fecha y hora de creación de la cuenta.
- `rol`: tipo de usuario, que puede ser `ADMINISTRADOR` o `CLIENTE`.

### Producto

Representa una versión concreta de una carta disponible en la tienda. Una misma carta puede aparecer varias veces si cambia el idioma, el estado o el acabado.

**Campos principales:**

- `id`: identificador único.
- `carta_externa_id`: identificador utilizado para consultar los datos de la carta en una fuente externa.
- `idioma`: idioma de la carta.
- `estado_carta`: estado de conservación.
- `acabado`: acabado de la carta.
- `precio`: precio de venta.
- `stock`: unidades disponibles.
- `activo`: indica si el producto está disponible para la venta.
- `fecha_creacion`: fecha de alta.
- `fecha_actualizacion`: fecha de la última modificación.

La combinación de `carta_externa_id`, `idioma`, `estado_carta` y `acabado` debe ser única.

### Direccion

Almacena las direcciones de envío asociadas a los usuarios.

**Campos principales:**

- `id`: identificador único.
- `usuario_id`: usuario propietario de la dirección.
- `nombre_destinatario`: nombre de la persona que recibe el pedido.
- `calle`, `numero` y `piso`: datos de la vía.
- `ciudad`, `provincia`, `codigo_postal` y `pais`: datos de localización.
- `principal`: indica si es la dirección principal del usuario.

### Carrito

Representa el carrito de compra de un usuario. Cada usuario solo puede tener un carrito.

**Campos principales:**

- `id`: identificador único.
- `usuario_id`: usuario propietario del carrito.
- `fecha_creacion`: fecha de creación.
- `fecha_actualizacion`: fecha de la última modificación.

### DetalleCarrito

Relaciona los productos con los carritos y almacena la cantidad seleccionada de cada producto.

**Campos principales:**

- `carrito_id`: carrito al que pertenece el producto.
- `producto_id`: producto añadido.
- `cantidad`: número de unidades.

Su clave primaria está formada por `carrito_id` y `producto_id`.

### Pedido

Registra las compras realizadas por los usuarios.

**Campos principales:**

- `id`: identificador único.
- `comprador_id`: usuario que realiza la compra.
- `direccion_envio_id`: dirección asociada al pedido.
- `fecha_pedido`: fecha y hora de creación.
- `estado`: situación actual del pedido.
- Datos de envío: copia de los datos del destinatario y su dirección.
- `subtotal`: importe de los productos.
- `gastos_envio`: coste del envío.
- `total`: suma del subtotal y los gastos de envío.

Los datos de envío se copian en el pedido para conservar el historial aunque posteriormente el usuario modifique o elimine su dirección.

### DetallePedido

Almacena los productos incluidos en cada pedido.

**Campos principales:**

- `id`: identificador único.
- `pedido_id`: pedido al que pertenece.
- `producto_id`: producto original, si continúa existiendo.
- `carta_externa_id`: identificador externo de la carta.
- `nombre_carta`, `nombre_set` y `numero_carta`: copia histórica de los datos principales.
- `idioma`, `estado_carta` y `acabado`: características de la carta comprada.
- `cantidad`: unidades adquiridas.
- `precio_unitario`: precio de cada unidad en el momento de la compra.
- `subtotal`: cantidad multiplicada por el precio unitario.

La copia histórica permite conservar los datos de la compra aunque cambien el producto o la fuente externa.

### Pago

Registra los intentos de pago asociados a los pedidos. Un pedido puede tener varios intentos de pago.

**Campos principales:**

- `id`: identificador único.
- `pedido_id`: pedido asociado.
- `metodo`: `TARJETA`, `PAYPAL` o `TRANSFERENCIA`.
- `estado`: estado del intento de pago.
- `importe`: cantidad correspondiente al pago.
- `referencia_pago`: referencia única de la operación.
- `fecha_pago`: fecha y hora del pago, si se ha realizado.

### Favorito

Relaciona usuarios con cartas marcadas como favoritas.

**Campos principales:**

- `usuario_id`: usuario que guarda el favorito.
- `carta_externa_id`: identificador externo de la carta.
- `fecha_agregado`: fecha en la que se añadió.

Su clave primaria está formada por `usuario_id` y `carta_externa_id`.

### Incidencia

Almacena reclamaciones o problemas relacionados con los pedidos.

**Campos principales:**

- `id`: identificador único.
- `pedido_id`: pedido relacionado.
- `usuario_id`: usuario que registra la incidencia.
- `descripcion`: explicación del problema.
- `estado`: `ABIERTA`, `EN_REVISION`, `RESUELTA` o `CERRADA`.
- `fecha_incidencia`: fecha de creación.
- `fecha_resolucion`: fecha de resolución, si existe.

## Relaciones principales

- Un usuario puede tener varias direcciones.
- Cada usuario puede tener un único carrito.
- Un carrito puede contener varios productos.
- Un producto puede aparecer en distintos carritos.
- Un usuario puede realizar varios pedidos.
- Un pedido puede contener varios detalles de pedido.
- Un producto puede aparecer en diferentes pedidos.
- Un pedido puede tener varios intentos de pago.
- Un usuario puede marcar varias cartas como favoritas.
- Una carta puede ser favorita de varios usuarios.
- Un pedido puede tener varias incidencias.
- Un usuario puede registrar varias incidencias.

## Integridad y validaciones

La base de datos incluye las siguientes medidas de integridad:

- Los nombres de usuario, correos electrónicos y teléfonos no pueden repetirse.
- Los precios, importes, subtotales, gastos de envío y totales no pueden ser negativos.
- La cantidad de un producto en un carrito o pedido debe ser superior a cero.
- El total de un pedido debe ser igual al subtotal más los gastos de envío.
- El subtotal de un detalle de pedido debe coincidir con la cantidad multiplicada por el precio unitario.
- La fecha de resolución de una incidencia no puede ser anterior a su fecha de creación.
- Las claves foráneas evitan referencias a registros inexistentes.

## Instalación y ejecución

1. Iniciar el servidor MySQL.
2. Ejecutar `CardTradeDB.sql` para crear la base de datos y sus tablas.
3. Ejecutar `inserts.sql` para cargar los datos de prueba.
4. Revisar las consultas `SELECT` situadas al final de `inserts.sql` para comprobar los registros insertados.

Ejemplo desde la consola:

```bash
mysql -u root -p < CardTradeDB.sql
mysql -u root -p tienda_cartas < inserts.sql
```

> Aviso: `CardTradeDB.sql` elimina la base de datos `tienda_cartas` si ya existe. Además, `inserts.sql` vacía todas las tablas antes de cargar los datos de prueba. No deben ejecutarse sobre una base de datos con información que se quiera conservar.

## Datos de prueba

El archivo `inserts.sql` incluye usuarios, productos, direcciones, carritos, pedidos, detalles, pagos, favoritos e incidencias de ejemplo.

La contraseña indicada para las cuentas de prueba es `password`, pero en la base de datos solo se almacena su hash bcrypt.

## Backend previsto

Para conectar el frontend con la base de datos se puede utilizar una aplicación Java como capa backend. Esta aplicación debe:

1. Recibir los datos enviados desde el frontend.
2. Validar los campos antes de procesarlos.
3. Insertar o modificar los registros mediante consultas preparadas o un sistema ORM.
4. Comunicar al frontend si la operación se ha completado correctamente.
5. Gestionar los errores producidos por duplicados, restricciones o relaciones inexistentes.
6. Generar una exportación `inserts.sql` a partir de los datos finales almacenados en MySQL cuando sea necesario.

La base de datos debe ser la fuente principal de información. El archivo `inserts.sql` debe utilizarse como carga inicial, copia reproducible o exportación, no como sustituto de la base de datos.

## Objetivo

El objetivo de CardTradeDB es ofrecer una estructura organizada y coherente para gestionar las operaciones principales de una tienda de cartas coleccionables, manteniendo la integridad de los datos y facilitando su futura conexión con un frontend y un backend Java.
