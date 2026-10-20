# Base de Datos - Marketplace de Cartas Coleccionables

## Descripción

Esta base de datos da soporte a una plataforma de compraventa de cartas coleccionables entre usuarios. Permite gestionar usuarios, anuncios de venta, pedidos, mensajería, valoraciones, incidencias, verificaciones y un sistema de favoritos y cashback.

---

## Entidades

### Usuario

Almacena la información de las personas registradas en la plataforma.

**Campos principales:**
- id
- usuario
- email
- telefono
- password
- fecha_registro
- rol

### Anuncio

Contiene los anuncios de cartas publicados por los vendedores.

**Campos principales:**
- id
- vendedor_id
- nombre_carta
- numero_carta
- coleccion
- rareza
- precio
- estado
- descripcion
- fecha_publicacion

### Pedido

Registra las compras realizadas por los usuarios.

**Campos principales:**
- id
- comprador_id
- fecha_pedido
- estado
- total

### Mensaje

Permite la comunicación entre usuarios de la plataforma.

**Campos principales:**
- id
- emisor_id
- receptor_id
- contenido
- fecha_envio

### Valoracion

Almacena las reseñas y puntuaciones entre usuarios.

**Campos principales:**
- id
- autor_id
- destinatario_id
- puntuacion
- comentario
- fecha_valoracion

### Cashback

Registra los importes devueltos a los usuarios según las condiciones de la plataforma.

**Campos principales:**
- id
- usuario_id
- pedido_id
- importe
- tipo

### VerificacionUsuario

Gestiona los distintos niveles de verificación de los usuarios.

**Campos principales:**
- id
- usuario_id
- nivel
- estado

### Incidencia

Almacena reclamaciones o problemas asociados a pedidos.

**Campos principales:**
- id
- pedido_id
- usuario_id
- descripcion
- estado
- fecha_incidencia

### Favorito

Relaciona usuarios con anuncios marcados como favoritos.

**Clave primaria compuesta:**
- usuario_id
- anuncio_id

---

## Relaciones

- Un usuario puede publicar múltiples anuncios.
- Un usuario puede realizar múltiples pedidos.
- Un usuario puede enviar y recibir múltiples mensajes.
- Un usuario puede emitir y recibir múltiples valoraciones.
- Un usuario puede tener procesos de verificación asociados.
- Un usuario puede registrar incidencias.
- Un usuario puede marcar anuncios como favoritos.
- Un pedido puede estar asociado a incidencias y operaciones de cashback.

---

## Objetivo de la Base de Datos

El propósito de esta base de datos es proporcionar una estructura organizada para gestionar todas las operaciones de una plataforma de compraventa de cartas coleccionables, garantizando la integridad de los datos mediante claves primarias, claves foráneas y restricciones de validación.