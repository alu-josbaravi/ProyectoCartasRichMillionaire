-- =========================
-- LIMPIEZA DE DATOS
-- =========================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE Favorito;
TRUNCATE TABLE Incidencia;
TRUNCATE TABLE VerificacionUsuario;
TRUNCATE TABLE Cashback;
TRUNCATE TABLE Valoracion;
TRUNCATE TABLE Mensaje;
TRUNCATE TABLE Pedido;
TRUNCATE TABLE Anuncio;
TRUNCATE TABLE Usuario;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- USUARIOS
-- =========================

INSERT INTO Usuario
(id, usuario, email, telefono, password, fecha_registro, rol)
VALUES
    (1, 'AdminTCG', 'admin@cartas.com', '600111111', 'admin123', '2026-01-10', 'ADMINISTRADOR');

INSERT INTO Usuario
(id, usuario, email, telefono, password, fecha_registro, rol)
VALUES
    (2, 'Pikafan', 'pikafan@gmail.com', '600222222', 'pass123', '2026-02-15', 'VERIFICADO');

INSERT INTO Usuario
(id, usuario, email, telefono, password, fecha_registro, rol)
VALUES
    (3, 'DragonMaster', 'dragonmaster@gmail.com', '600333333', 'pass123', '2026-03-02', 'VERIFICADO');

INSERT INTO Usuario
(id, usuario, email, telefono, password, fecha_registro, rol)
VALUES
    (4, 'Coleccionista', 'coleccionista@gmail.com', '600444444', 'pass123', '2026-03-20', 'VISITANTE');

INSERT INTO Usuario
(id, usuario, email, telefono, password, fecha_registro, rol)
VALUES
    (5, 'MewCollector', 'mewcollector@gmail.com', '600555555', 'pass123', '2026-04-10', 'VERIFICADO');

-- =========================
-- ANUNCIOS
-- =========================

INSERT INTO Anuncio
(id, vendedor_id, nombre_carta, numero_carta, coleccion, rareza, precio, estado, descripcion, fecha_publicacion)
VALUES
    (1, 2, 'Charizard EX', '006/165', 'Scarlet & Violet', 'Ultra Rara',
     199.99, 'Excelente',
     'Carta en perfecto estado, enfundada desde apertura.',
     '2026-05-01');

INSERT INTO Anuncio
(id, vendedor_id, nombre_carta, numero_carta, coleccion, rareza, precio, estado, descripcion, fecha_publicacion)
VALUES
    (2, 3, 'Blue-Eyes White Dragon', 'SDK-001', 'Starter Deck Kaiba', 'Ultra Rara',
     49.95, 'Muy Bueno',
     'Primera edición.',
     '2026-05-05');

INSERT INTO Anuncio
(id, vendedor_id, nombre_carta, numero_carta, coleccion, rareza, precio, estado, descripcion, fecha_publicacion)
VALUES
    (3, 5, 'Mew VMAX', '114/264', 'Fusion Strike', 'Ultra Rara',
     29.99, 'Excelente',
     'Carta competitiva.',
     '2026-05-12');

-- =========================
-- PEDIDOS
-- =========================

INSERT INTO Pedido
(id, comprador_id, fecha_pedido, estado, total)
VALUES
    (1, 4, '2026-05-15', 'ENTREGADO', 199.99);

INSERT INTO Pedido
(id, comprador_id, fecha_pedido, estado, total)
VALUES
    (2, 2, '2026-05-20', 'ENVIADO', 49.95);

INSERT INTO Pedido
(id, comprador_id, fecha_pedido, estado, total)
VALUES
    (3, 4, '2026-05-25', 'PENDIENTE', 29.99);

-- =========================
-- MENSAJES
-- =========================

INSERT INTO Mensaje
(id, emisor_id, receptor_id, contenido, fecha_envio)
VALUES
    (1, 4, 2, '¿La carta Charizard sigue disponible?', '2026-05-10');

INSERT INTO Mensaje
(id, emisor_id, receptor_id, contenido, fecha_envio)
VALUES
    (2, 2, 4, 'Sí, sigue disponible.', '2026-05-10');

INSERT INTO Mensaje
(id, emisor_id, receptor_id, contenido, fecha_envio)
VALUES
    (3, 4, 2, 'Perfecto, realizaré el pedido.', '2026-05-10');

-- =========================
-- VALORACIONES
-- =========================

INSERT INTO Valoracion
(id, autor_id, destinatario_id, puntuacion, comentario, fecha_valoracion)
VALUES
    (1, 4, 2, 5, 'Vendedor muy recomendable.', '2026-05-18');

INSERT INTO Valoracion
(id, autor_id, destinatario_id, puntuacion, comentario, fecha_valoracion)
VALUES
    (2, 2, 4, 5, 'Comprador serio y rápido.', '2026-05-18');

INSERT INTO Valoracion
(id, autor_id, destinatario_id, puntuacion, comentario, fecha_valoracion)
VALUES
    (3, 4, 3, 4, 'Buena comunicación.', '2026-05-30');

-- =========================
-- CASHBACK
-- =========================

INSERT INTO Cashback
(id, usuario_id, pedido_id, importe, tipo)
VALUES
    (1, 4, 1, 10.00, 'COMPRA');

INSERT INTO Cashback
(id, usuario_id, pedido_id, importe, tipo)
VALUES
    (2, 2, 2, 2.50, 'PROMOCION');

-- =========================
-- VERIFICACIONES
-- =========================

INSERT INTO VerificacionUsuario
(id, usuario_id, nivel, estado)
VALUES
    (1, 2, 2, 'APROBADA');

INSERT INTO VerificacionUsuario
(id, usuario_id, nivel, estado)
VALUES
    (2, 3, 1, 'APROBADA');

INSERT INTO VerificacionUsuario
(id, usuario_id, nivel, estado)
VALUES
    (3, 5, 3, 'PENDIENTE');

-- =========================
-- INCIDENCIAS
-- =========================

INSERT INTO Incidencia
(id, pedido_id, usuario_id, descripcion, estado, fecha_incidencia)
VALUES
    (1, 2, 2, 'Retraso en la entrega del pedido.', 'ABIERTA', '2026-05-25');

INSERT INTO Incidencia
(id, pedido_id, usuario_id, descripcion, estado, fecha_incidencia)
VALUES
    (2, 1, 4, 'Consulta sobre el estado del envío.', 'CERRADA', '2026-05-16');

-- =========================
-- FAVORITOS
-- =========================

INSERT INTO Favorito
(usuario_id, anuncio_id)
VALUES
    (4, 1);

INSERT INTO Favorito
(usuario_id, anuncio_id)
VALUES
    (4, 3);

INSERT INTO Favorito
(usuario_id, anuncio_id)
VALUES
    (2, 2);