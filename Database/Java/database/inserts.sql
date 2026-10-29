USE tienda_cartas;
SET FOREIGN_KEY_CHECKS=0;
DELETE FROM Incidencia;DELETE FROM Favorito;DELETE FROM Pago;DELETE FROM DetallePedido;DELETE FROM Pedido;DELETE FROM DetalleCarrito;DELETE FROM Producto;DELETE FROM Carrito;DELETE FROM Direccion;DELETE FROM Usuario;
SET FOREIGN_KEY_CHECKS=1;
INSERT INTO Usuario(usuario,email,telefono,password,rol) VALUES
('admin','admin@cardtrade.local','600000001','$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.','ADMINISTRADOR'),
('cliente','cliente@cardtrade.local','600000002','$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.','CLIENTE');
INSERT INTO Carrito(usuario_id) SELECT id FROM Usuario;
INSERT INTO Producto(carta_externa_id,idioma,estado_carta,acabado,precio,stock,activo) VALUES
('base1-4','es','EXCELENTE','HOLO',120.50,2,TRUE),('sv3pt5-25','en','NUEVA','NORMAL',2.95,15,TRUE);
SELECT id,usuario,email,rol FROM Usuario;SELECT id,carta_externa_id,precio,stock FROM Producto;
