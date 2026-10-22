-- =====================================================
-- ELIMINAR Y CREAR LA BASE DE DATOS
-- =====================================================

DROP DATABASE IF EXISTS tienda_cartas;

CREATE DATABASE tienda_cartas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE tienda_cartas;

-- =====================================================
-- USUARIOS
-- =====================================================

CREATE TABLE Usuario (
                         id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                         usuario VARCHAR(16) NOT NULL UNIQUE,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         telefono VARCHAR(20) UNIQUE,
                         password VARCHAR(255) NOT NULL,

                         fecha_registro DATETIME NOT NULL
                                        DEFAULT CURRENT_TIMESTAMP,

                         rol ENUM(
                             'ADMINISTRADOR',
                             'CLIENTE'
                             ) NOT NULL DEFAULT 'CLIENTE'
) ENGINE = InnoDB;


-- =====================================================
-- PRODUCTOS
--
-- No almacena nombre, set, rareza ni imagen.
-- Esa información se obtiene del GitHub o de la API
-- utilizando carta_externa_id.
-- =====================================================

CREATE TABLE Producto (
                          id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    -- Ejemplos: base1-4, xy1-1, swsh1-1
                          carta_externa_id VARCHAR(100) NOT NULL,

                          idioma VARCHAR(10) NOT NULL DEFAULT 'es',

                          estado_carta ENUM(
                              'NUEVA',
                              'EXCELENTE',
                              'BUENA',
                              'JUGADA',
                              'DETERIORADA'
                              ) NOT NULL,

                          acabado ENUM(
                              'NORMAL',
                              'HOLO',
                              'REVERSE_HOLO'
                              ) NOT NULL DEFAULT 'NORMAL',

                          precio DECIMAL(10,2) NOT NULL,
                          stock INT UNSIGNED NOT NULL DEFAULT 0,
                          activo BOOLEAN NOT NULL DEFAULT TRUE,

                          fecha_creacion DATETIME NOT NULL
                                                      DEFAULT CURRENT_TIMESTAMP,

                          fecha_actualizacion DATETIME NOT NULL
                                                      DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,

                          CONSTRAINT uq_producto_version
                              UNIQUE (
                                      carta_externa_id,
                                      idioma,
                                      estado_carta,
                                      acabado
                              ),

                          CHECK (precio >= 0),

                          INDEX idx_producto_carta_externa (carta_externa_id),
                          INDEX idx_producto_precio (precio),
                          INDEX idx_producto_disponible (activo, stock)
) ENGINE = InnoDB;


-- =====================================================
-- DIRECCIONES
-- =====================================================

CREATE TABLE Direccion (
                           id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                           usuario_id INT UNSIGNED NOT NULL,

                           nombre_destinatario VARCHAR(255) NOT NULL,
                           calle VARCHAR(255) NOT NULL,
                           numero VARCHAR(20),
                           piso VARCHAR(20),
                           ciudad VARCHAR(100) NOT NULL,
                           provincia VARCHAR(100),
                           codigo_postal VARCHAR(20) NOT NULL,
                           pais VARCHAR(100) NOT NULL DEFAULT 'España',

                           principal BOOLEAN NOT NULL DEFAULT FALSE,

                           CONSTRAINT fk_direccion_usuario
                               FOREIGN KEY (usuario_id)
                                   REFERENCES Usuario(id)
                                   ON UPDATE CASCADE
                                   ON DELETE CASCADE,

                           INDEX idx_direccion_usuario (usuario_id)
) ENGINE = InnoDB;


-- =====================================================
-- CARRITOS
-- Cada usuario tiene un único carrito.
-- =====================================================

CREATE TABLE Carrito (
                         id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                         usuario_id INT UNSIGNED NOT NULL UNIQUE,

                         fecha_creacion DATETIME NOT NULL
                             DEFAULT CURRENT_TIMESTAMP,

                         fecha_actualizacion DATETIME NOT NULL
                             DEFAULT CURRENT_TIMESTAMP
                             ON UPDATE CURRENT_TIMESTAMP,

                         CONSTRAINT fk_carrito_usuario
                             FOREIGN KEY (usuario_id)
                                 REFERENCES Usuario(id)
                                 ON UPDATE CASCADE
                                 ON DELETE CASCADE
) ENGINE = InnoDB;


-- =====================================================
-- PRODUCTOS DEL CARRITO
-- =====================================================

CREATE TABLE DetalleCarrito (
                                carrito_id INT UNSIGNED NOT NULL,
                                producto_id INT UNSIGNED NOT NULL,
                                cantidad INT UNSIGNED NOT NULL DEFAULT 1,

                                PRIMARY KEY (
                                             carrito_id,
                                             producto_id
                                    ),

                                CONSTRAINT fk_detalle_carrito
                                    FOREIGN KEY (carrito_id)
                                        REFERENCES Carrito(id)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_detalle_carrito_producto
                                    FOREIGN KEY (producto_id)
                                        REFERENCES Producto(id)
                                        ON UPDATE CASCADE
                                        ON DELETE CASCADE,

                                CHECK (cantidad > 0),

                                INDEX idx_detalle_carrito_producto (producto_id)
) ENGINE = InnoDB;


-- =====================================================
-- PEDIDOS
--
-- Almacena una copia de la dirección para mantener
-- el historial aunque el usuario modifique su dirección.
-- =====================================================

CREATE TABLE Pedido (
                        id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                        comprador_id INT UNSIGNED NOT NULL,
                        direccion_envio_id INT UNSIGNED,

                        fecha_pedido DATETIME NOT NULL
                                                            DEFAULT CURRENT_TIMESTAMP,

                        estado ENUM(
                            'PENDIENTE',
                            'PAGADO',
                            'PREPARANDO',
                            'ENVIADO',
                            'ENTREGADO',
                            'CANCELADO'
                            ) NOT NULL DEFAULT 'PENDIENTE',

                        nombre_destinatario VARCHAR(255) NOT NULL,
                        calle_envio VARCHAR(255) NOT NULL,
                        numero_envio VARCHAR(20),
                        piso_envio VARCHAR(20),
                        ciudad_envio VARCHAR(100) NOT NULL,
                        provincia_envio VARCHAR(100),
                        codigo_postal_envio VARCHAR(20) NOT NULL,
                        pais_envio VARCHAR(100) NOT NULL,

                        subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
                        gastos_envio DECIMAL(10,2) NOT NULL DEFAULT 0,
                        total DECIMAL(10,2) NOT NULL DEFAULT 0,

                        CONSTRAINT fk_pedido_comprador
                            FOREIGN KEY (comprador_id)
                                REFERENCES Usuario(id)
                                ON UPDATE CASCADE
                                ON DELETE RESTRICT,

                        CONSTRAINT fk_pedido_direccion
                            FOREIGN KEY (direccion_envio_id)
                                REFERENCES Direccion(id)
                                ON UPDATE CASCADE
                                ON DELETE SET NULL,

                        CHECK (subtotal >= 0),
                        CHECK (gastos_envio >= 0),
                        CHECK (total >= 0),
                        CHECK (total = subtotal + gastos_envio),

                        INDEX idx_pedido_comprador (comprador_id),
                        INDEX idx_pedido_estado (estado),
                        INDEX idx_pedido_fecha (fecha_pedido)
) ENGINE = InnoDB;


-- =====================================================
-- DETALLES DEL PEDIDO
--
-- Conserva una copia histórica de los datos principales.
-- Esto es intencionado, no redundancia innecesaria.
--
-- Si el GitHub o la API cambian, el pedido seguirá
-- mostrando qué compró el cliente.
-- =====================================================

CREATE TABLE DetallePedido (
                               id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                               pedido_id INT UNSIGNED NOT NULL,

    -- El producto puede quedar eliminado posteriormente.
                               producto_id INT UNSIGNED,

                               carta_externa_id VARCHAR(100) NOT NULL,

                               nombre_carta VARCHAR(255) NOT NULL,
                               nombre_set VARCHAR(255),
                               numero_carta VARCHAR(30),
                               idioma VARCHAR(10) NOT NULL,

                               estado_carta ENUM(
                                   'NUEVA',
                                   'EXCELENTE',
                                   'BUENA',
                                   'JUGADA',
                                   'DETERIORADA'
                                   ) NOT NULL,

                               acabado ENUM(
                                   'NORMAL',
                                   'HOLO',
                                   'REVERSE_HOLO'
                                   ) NOT NULL,

                               cantidad INT UNSIGNED NOT NULL,
                               precio_unitario DECIMAL(10,2) NOT NULL,
                               subtotal DECIMAL(10,2) NOT NULL,

                               CONSTRAINT fk_detalle_pedido
                                   FOREIGN KEY (pedido_id)
                                       REFERENCES Pedido(id)
                                       ON UPDATE CASCADE
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_detalle_pedido_producto
                                   FOREIGN KEY (producto_id)
                                       REFERENCES Producto(id)
                                       ON UPDATE CASCADE
                                       ON DELETE SET NULL,

                               CONSTRAINT uq_detalle_pedido_producto
                                   UNIQUE (
                                           pedido_id,
                                           carta_externa_id,
                                           idioma,
                                           estado_carta,
                                           acabado
                                   ),

                               CHECK (cantidad > 0),
                               CHECK (precio_unitario >= 0),
                               CHECK (subtotal >= 0),
                               CHECK (subtotal = cantidad * precio_unitario),

                               INDEX idx_detalle_pedido_producto (producto_id),
                               INDEX idx_detalle_pedido_carta (carta_externa_id)
) ENGINE = InnoDB;


-- =====================================================
-- PAGOS
-- Un pedido puede tener varios intentos de pago.
-- =====================================================

CREATE TABLE Pago (
                      id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                      pedido_id INT UNSIGNED NOT NULL,

                      metodo ENUM(
                          'TARJETA',
                          'PAYPAL',
                          'TRANSFERENCIA'
                          ) NOT NULL,

                      estado ENUM(
                          'PENDIENTE',
                          'COMPLETADO',
                          'RECHAZADO',
                          'REEMBOLSADO'
                          ) NOT NULL DEFAULT 'PENDIENTE',

                      importe DECIMAL(10,2) NOT NULL,
                      referencia_pago VARCHAR(255) UNIQUE,
                      fecha_pago DATETIME,

                      CONSTRAINT fk_pago_pedido
                          FOREIGN KEY (pedido_id)
                              REFERENCES Pedido(id)
                              ON UPDATE CASCADE
                              ON DELETE CASCADE,

                      CHECK (importe >= 0),

                      INDEX idx_pago_pedido (pedido_id),
                      INDEX idx_pago_estado (estado)
) ENGINE = InnoDB;


-- =====================================================
-- FAVORITOS
--
-- Solo almacena el ID externo. El nombre y la imagen
-- se consultan desde el GitHub o desde la API.
-- =====================================================

CREATE TABLE Favorito (
                          usuario_id INT UNSIGNED NOT NULL,
                          carta_externa_id VARCHAR(100) NOT NULL,

                          fecha_agregado DATETIME NOT NULL
                              DEFAULT CURRENT_TIMESTAMP,

                          PRIMARY KEY (
                                       usuario_id,
                                       carta_externa_id
                              ),

                          CONSTRAINT fk_favorito_usuario
                              FOREIGN KEY (usuario_id)
                                  REFERENCES Usuario(id)
                                  ON UPDATE CASCADE
                                  ON DELETE CASCADE,

                          INDEX idx_favorito_carta_externa (carta_externa_id)
) ENGINE = InnoDB;


-- =====================================================
-- INCIDENCIAS
-- =====================================================

CREATE TABLE Incidencia (
                            id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                            pedido_id INT UNSIGNED NOT NULL,
                            usuario_id INT UNSIGNED NOT NULL,

                            descripcion TEXT NOT NULL,

                            estado ENUM(
                                'ABIERTA',
                                'EN_REVISION',
                                'RESUELTA',
                                'CERRADA'
                                ) NOT NULL DEFAULT 'ABIERTA',

                            fecha_incidencia DATETIME NOT NULL
                                           DEFAULT CURRENT_TIMESTAMP,

                            fecha_resolucion DATETIME,

                            CONSTRAINT fk_incidencia_pedido
                                FOREIGN KEY (pedido_id)
                                    REFERENCES Pedido(id)
                                    ON UPDATE CASCADE
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_incidencia_usuario
                                FOREIGN KEY (usuario_id)
                                    REFERENCES Usuario(id)
                                    ON UPDATE CASCADE
                                    ON DELETE RESTRICT,

                            CHECK (
                                fecha_resolucion IS NULL
                                    OR fecha_resolucion >= fecha_incidencia
                                ),

                            INDEX idx_incidencia_pedido (pedido_id),
                            INDEX idx_incidencia_usuario (usuario_id),
                            INDEX idx_incidencia_estado (estado)
) ENGINE = InnoDB;