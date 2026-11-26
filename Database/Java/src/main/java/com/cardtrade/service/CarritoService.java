package com.cardtrade.service;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CarritoService {

    private final JdbcClient jdbc;

    public CarritoService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    private long carritoId(long usuarioId) {
        return jdbc.sql("SELECT id FROM Carrito WHERE usuario_id = ?")
                .param(usuarioId)
                .query(Long.class)
                .optional()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario o carrito no encontrado"));
    }

    public Map<String, Object> obtener(long usuarioId) {

        long carritoId = carritoId(usuarioId);

        List<Map<String, Object>> filas = jdbc.sql("""
                SELECT p.id AS producto_id, p.carta_externa_id, p.idioma, p.estado_carta,
                       p.acabado, p.precio, p.stock, d.cantidad
                FROM DetalleCarrito d
                JOIN Producto p ON p.id = d.producto_id
                WHERE d.carrito_id = ?
                ORDER BY p.id
                """)
                .param(carritoId)
                .query()
                .listOfRows();

        List<Map<String, Object>> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        int unidades = 0;

        for (Map<String, Object> fila : filas) {
            int cantidad = ((Number) fila.get("cantidad")).intValue();
            BigDecimal precio = new BigDecimal(fila.get("precio").toString());
            BigDecimal importe = precio.multiply(BigDecimal.valueOf(cantidad));

            Map<String, Object> item = new LinkedHashMap<>(fila);
            item.put("subtotal", importe);
            items.add(item);

            subtotal = subtotal.add(importe);
            unidades += cantidad;
        }

        BigDecimal envio = PedidoService.calcularEnvio(subtotal);

        Map<String, Object> carrito = new LinkedHashMap<>();
        carrito.put("items", items);
        carrito.put("unidades", unidades);
        carrito.put("subtotal", subtotal);
        carrito.put("gastosEnvio", envio);
        carrito.put("total", subtotal.add(envio));
        return carrito;
    }

    /** Suma "cantidad" unidades al producto (lo crea en el carrito si no estaba). */
    @Transactional
    public Map<String, Object> anadir(long usuarioId, long productoId, int cantidad) {
        long carritoId = carritoId(usuarioId);
        int actual = cantidadActual(carritoId, productoId);
        fijar(carritoId, productoId, actual + cantidad, actual > 0);
        return obtener(usuarioId);
    }

    /** Establece la cantidad exacta de un producto del carrito. */
    @Transactional
    public Map<String, Object> cambiarCantidad(long usuarioId, long productoId, int cantidad) {
        long carritoId = carritoId(usuarioId);
        int actual = cantidadActual(carritoId, productoId);
        fijar(carritoId, productoId, cantidad, actual > 0);
        return obtener(usuarioId);
    }

    @Transactional
    public Map<String, Object> quitar(long usuarioId, long productoId) {
        long carritoId = carritoId(usuarioId);
        jdbc.sql("DELETE FROM DetalleCarrito WHERE carrito_id = ? AND producto_id = ?")
                .params(carritoId, productoId)
                .update();
        return obtener(usuarioId);
    }

    private int cantidadActual(long carritoId, long productoId) {
        return jdbc.sql("SELECT cantidad FROM DetalleCarrito WHERE carrito_id = ? AND producto_id = ?")
                .params(carritoId, productoId)
                .query(Integer.class)
                .optional()
                .orElse(0);
    }

    private void fijar(long carritoId, long productoId, int cantidad, boolean yaExiste) {

        // Solo productos activos
        Integer stock = jdbc.sql("SELECT stock FROM Producto WHERE id = ? AND activo = TRUE")
                .param(productoId)
                .query(Integer.class)
                .optional()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no disponible"));

        if (cantidad > stock) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "No hay stock suficiente (disponible: " + stock + ")");
        }

        if (yaExiste) {
            jdbc.sql("UPDATE DetalleCarrito SET cantidad = ? WHERE carrito_id = ? AND producto_id = ?")
                    .params(cantidad, carritoId, productoId)
                    .update();
        } else {
            jdbc.sql("INSERT INTO DetalleCarrito(carrito_id, producto_id, cantidad) VALUES (?, ?, ?)")
                    .params(carritoId, productoId, cantidad)
                    .update();
        }
    }
}
