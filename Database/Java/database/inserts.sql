USE tienda_cartas;


-- =====================================================
-- ELIMINAR TODOS LOS DATOS ANTERIORES
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE Incidencia;
TRUNCATE TABLE Favorito;
TRUNCATE TABLE Pago;
TRUNCATE TABLE DetallePedido;
TRUNCATE TABLE Pedido;
TRUNCATE TABLE DetalleCarrito;
TRUNCATE TABLE Carrito;
TRUNCATE TABLE Direccion;
TRUNCATE TABLE Producto;
TRUNCATE TABLE Usuario;

SET FOREIGN_KEY_CHECKS = 1;


-- =====================================================
-- INICIAR TRANSACCIÓN
-- =====================================================

START TRANSACTION;


-- =====================================================
-- USUARIOS
--
-- La contraseña de prueba es: password
-- El valor almacenado es un hash bcrypt.
-- =====================================================

INSERT INTO Usuario (
    id,
    usuario,
    email,
    telefono,
    password,
    fecha_registro,
    rol
) VALUES
      (
          1,
          'admin',
          'admin@tiendacartas.es',
          '600000001',
          '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi',
          '2026-10-01 10:00:00',
          'ADMINISTRADOR'
      ),
      (
          2,
          'alejandro',
          'alejandro@example.com',
          '600000002',
          '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi',
          '2026-10-02 12:30:00',
          'CLIENTE'
      ),
      (
          3,
          'lucia',
          'lucia@example.com',
          '600000003',
          '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi',
          '2026-10-03 17:15:00',
          'CLIENTE'
      );


-- =====================================================
-- PRODUCTOS
--
-- Los datos de la carta no se duplican.
-- Solo se almacena su identificador externo.
-- =====================================================

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
          'en',
          'NUEVA',
          'HOLO',
          349.99,
          2,
          TRUE
      ),
      (
          2,
          'base1-4',
          'en',
          'EXCELENTE',
          'HOLO',
          189.99,
          3,
          TRUE
      ),
      (
          3,
          'base1-2',
          'en',
          'EXCELENTE',
          'HOLO',
          69.99,
          4,
          TRUE
      ),
      (
          4,
          'base1-15',
          'en',
          'BUENA',
          'HOLO',
          39.50,
          5,
          TRUE
      ),
      (
          5,
          'base1-58',
          'en',
          'NUEVA',
          'NORMAL',
          3.50,
          20,
          TRUE
      ),
      (
          6,
          'base1-63',
          'en',
          'EXCELENTE',
          'NORMAL',
          2.75,
          15,
          TRUE
      ),
      (
          7,
          'xy1-1',
          'en',
          'NUEVA',
          'HOLO',
          12.99,
          8,
          TRUE
      ),
      (
          8,
          'xy1-1',
          'en',
          'JUGADA',
          'HOLO',
          5.99,
          3,
          TRUE
      ),
      (
          9,
          'swsh1-1',
          'en',
          'NUEVA',
          'REVERSE_HOLO',
          4.50,
          10,
          TRUE
      ),
      (
          10,
          'swsh1-1',
          'en',
          'NUEVA',
          'NORMAL',
          1.25,
          25,
          TRUE
      ),
      (
          11,
          'base1-4',
          'es',
          'BUENA',
          'HOLO',
          149.99,
          1,
          TRUE
      ),
      (
          12,
          'base1-58',
          'en',
          'DETERIORADA',
          'NORMAL',
          0.50,
          0,
          FALSE
      );


-- =====================================================
-- DIRECCIONES
-- =====================================================

INSERT INTO Direccion (
    id,
    usuario_id,
    nombre_destinatario,
    calle,
    numero,
    piso,
    ciudad,
    provincia,
    codigo_postal,
    pais,
    principal
) VALUES
      (
          1,
          2,
          'Cliente de prueba uno',
          'Calle Granada',
          '15',
          '2B',
          'Maracena',
          'Granada',
          '18200',
          'España',
          TRUE
      ),
      (
          2,
          2,
          'Cliente de prueba uno',
          'Avenida de Andalucía',
          '42',
          NULL,
          'Granada',
          'Granada',
          '18014',
          'España',
          FALSE
      ),
      (
          3,
          3,
          'Cliente de prueba dos',
          'Calle de la Luna',
          '8',
          '1A',
          'Málaga',
          'Málaga',
          '29001',
          'España',
          TRUE
      );


-- =====================================================
-- CARRITOS
-- =====================================================

INSERT INTO Carrito (
    id,
    usuario_id,
    fecha_creacion,
    fecha_actualizacion
) VALUES
      (
          1,
          2,
          '2026-10-20 10:00:00',
          '2026-10-22 09:30:00'
      ),
      (
          2,
          3,
          '2026-10-21 16:00:00',
          '2026-10-22 11:15:00'
      );


-- =====================================================
-- DETALLES DE LOS CARRITOS
-- =====================================================

INSERT INTO DetalleCarrito (
    carrito_id,
    producto_id,
    cantidad
) VALUES
      (
          1,
          7,
          1
      ),
      (
          1,
          9,
          2
      ),
      (
          1,
          10,
          4
      ),
      (
          2,
          2,
          1
      ),
      (
          2,
          5,
          3
      );


-- =====================================================
-- PEDIDOS
-- =====================================================

INSERT INTO Pedido (
    id,
    comprador_id,
    direccion_envio_id,
    fecha_pedido,
    estado,
    nombre_destinatario,
    calle_envio,
    numero_envio,
    piso_envio,
    ciudad_envio,
    provincia_envio,
    codigo_postal_envio,
    pais_envio,
    subtotal,
    gastos_envio,
    total
) VALUES
      (
          1,
          2,
          1,
          '2026-10-10 12:00:00',
          'ENTREGADO',
          'Cliente de prueba uno',
          'Calle Granada',
          '15',
          '2B',
          'Maracena',
          'Granada',
          '18200',
          'España',
          18.49,
          3.99,
          22.48
      ),
      (
          2,
          3,
          3,
          '2026-10-18 17:30:00',
          'ENVIADO',
          'Cliente de prueba dos',
          'Calle de la Luna',
          '8',
          '1A',
          'Málaga',
          'Málaga',
          '29001',
          'España',
          76.99,
          3.99,
          80.98
      ),
      (
          3,
          2,
          2,
          '2026-10-22 09:00:00',
          'PENDIENTE',
          'Cliente de prueba uno',
          'Avenida de Andalucía',
          '42',
          NULL,
          'Granada',
          'Granada',
          '18014',
          'España',
          149.99,
          0.00,
          149.99
      );


-- =====================================================
-- DETALLES DE LOS PEDIDOS
--
-- Aquí sí se guarda el nombre y el set como copia
-- histórica de lo comprado.
-- =====================================================

INSERT INTO DetallePedido (
    id,
    pedido_id,
    producto_id,
    carta_externa_id,
    nombre_carta,
    nombre_set,
    numero_carta,
    idioma,
    estado_carta,
    acabado,
    cantidad,
    precio_unitario,
    subtotal
) VALUES
      (
          1,
          1,
          7,
          'xy1-1',
          'Venusaur-EX',
          'XY',
          '1',
          'en',
          'NUEVA',
          'HOLO',
          1,
          12.99,
          12.99
      ),
      (
          2,
          1,
          6,
          'base1-63',
          'Squirtle',
          'Base',
          '63',
          'en',
          'EXCELENTE',
          'NORMAL',
          2,
          2.75,
          5.50
      ),
      (
          3,
          2,
          3,
          'base1-2',
          'Blastoise',
          'Base',
          '2',
          'en',
          'EXCELENTE',
          'HOLO',
          1,
          69.99,
          69.99
      ),
      (
          4,
          2,
          5,
          'base1-58',
          'Pikachu',
          'Base',
          '58',
          'en',
          'NUEVA',
          'NORMAL',
          2,
          3.50,
          7.00
      ),
      (
          5,
          3,
          11,
          'base1-4',
          'Charizard',
          'Base',
          '4',
          'es',
          'BUENA',
          'HOLO',
          1,
          149.99,
          149.99
      );


-- =====================================================
-- PAGOS
-- =====================================================

INSERT INTO Pago (
    id,
    pedido_id,
    metodo,
    estado,
    importe,
    referencia_pago,
    fecha_pago
) VALUES
      (
          1,
          1,
          'TARJETA',
          'COMPLETADO',
          22.48,
          'PAGO-TARJETA-0001',
          '2026-10-10 12:05:00'
      ),
      (
          2,
          2,
          'PAYPAL',
          'COMPLETADO',
          80.98,
          'PAGO-PAYPAL-0002',
          '2026-10-18 17:35:00'
      ),
      (
          3,
          3,
          'TARJETA',
          'RECHAZADO',
          149.99,
          'PAGO-TARJETA-0003-A',
          '2026-10-22 09:02:00'
      ),
      (
          4,
          3,
          'TRANSFERENCIA',
          'PENDIENTE',
          149.99,
          'PAGO-TRANSFERENCIA-0003-B',
          NULL
      );


-- =====================================================
-- FAVORITOS
--
-- Solo almacenan el identificador externo.
-- =====================================================

INSERT INTO Favorito (
    usuario_id,
    carta_externa_id,
    fecha_agregado
) VALUES
      (
          2,
          'base1-4',
          '2026-10-15 10:30:00'
      ),
      (
          2,
          'base1-2',
          '2026-10-16 11:00:00'
      ),
      (
          2,
          'xy1-1',
          '2026-10-17 18:20:00'
      ),
      (
          3,
          'base1-4',
          '2026-10-18 09:45:00'
      ),
      (
          3,
          'swsh1-1',
          '2026-10-19 14:10:00'
      );


-- =====================================================
-- INCIDENCIAS
-- =====================================================

INSERT INTO Incidencia (
    id,
    pedido_id,
    usuario_id,
    descripcion,
    estado,
    fecha_incidencia,
    fecha_resolucion
) VALUES
      (
          1,
          1,
          2,
          'Una de las cartas llegó con una pequeña marca en una esquina.',
          'RESUELTA',
          '2026-10-14 10:00:00',
          '2026-10-15 16:30:00'
      ),
      (
          2,
          2,
          3,
          'El seguimiento del envío no se ha actualizado durante dos días.',
          'EN_REVISION',
          '2026-10-21 12:15:00',
          NULL
      );


-- =====================================================
-- CONFIRMAR LOS DATOS
-- =====================================================

COMMIT;


-- =====================================================
-- CONSULTAS DE COMPROBACIÓN
-- =====================================================

SELECT * FROM Usuario;
SELECT * FROM Producto;
SELECT * FROM Direccion;
SELECT * FROM Carrito;
SELECT * FROM DetalleCarrito;
SELECT * FROM Pedido;
SELECT * FROM DetallePedido;
SELECT * FROM Pago;
SELECT * FROM Favorito;
SELECT * FROM Incidencia;