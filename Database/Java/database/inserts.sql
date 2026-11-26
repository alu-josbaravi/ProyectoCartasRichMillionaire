-- =====================================================
-- CARDTRADEDB - DATOS INICIALES DEFINITIVOS
-- Ejecutar después de CardTradeDB.sql
-- Contraseña de ambos usuarios: password
-- =====================================================

USE tienda_cartas;

-- El borrado se hace en orden inverso a las dependencias.
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE Incidencia;
TRUNCATE TABLE Favorito;
TRUNCATE TABLE Pago;
TRUNCATE TABLE DetallePedido;
TRUNCATE TABLE Pedido;
TRUNCATE TABLE DetalleCarrito;
TRUNCATE TABLE Producto;
TRUNCATE TABLE Carrito;
TRUNCATE TABLE Direccion;
TRUNCATE TABLE Usuario;

SET FOREIGN_KEY_CHECKS = 1;

START TRANSACTION;

-- Hash bcrypt válido de la contraseña "password".
INSERT INTO Usuario (
    id,
    usuario,
    email,
    telefono,
    password,
    rol
) VALUES
    (
        1,
        'admin',
        'admin@cardtrade.local',
        '600000001',
        '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi',
        'ADMINISTRADOR'
    ),
    (
        2,
        'cliente',
        'cliente@cardtrade.local',
        '600000002',
        '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi',
        'CLIENTE'
    );

INSERT INTO Carrito (id, usuario_id) VALUES
    (1, 1),
    (2, 2);

INSERT INTO Producto (
    id,
    carta_externa_id,
    idioma,
    estado_carta,
    acabado,
    precio,
    stock,
    activo
) VALUES
    (
        1,
        'base1-4',
        'es',
        'EXCELENTE',
        'HOLO',
        120.50,
        2,
        TRUE
    ),
    (
        2,
        'sv3pt5-25',
        'en',
        'NUEVA',
        'NORMAL',
        2.95,
        15,
        TRUE
    );

COMMIT;

-- =====================================================
-- COMPROBACIONES
-- =====================================================

SELECT id, usuario, email, rol
FROM Usuario
ORDER BY id;

SELECT id, usuario_id
FROM Carrito
ORDER BY id;

SELECT id, carta_externa_id, idioma, estado_carta,
       acabado, precio, stock, activo
FROM Producto
ORDER BY id;
