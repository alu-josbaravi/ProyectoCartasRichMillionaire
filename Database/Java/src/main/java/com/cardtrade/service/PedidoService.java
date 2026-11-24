package com.cardtrade.service;

import com.cardtrade.dto.PedidoRequest;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    // REGLA DE NEGOCIO DE EJEMPLO: cambiad estos valores a los que decidais para la tienda
    private static final BigDecimal GASTOS_ENVIO = new BigDecimal("4.99");
    private static final BigDecimal ENVIO_GRATIS_DESDE = new BigDecimal("50.00");

    private final JdbcClient jdbc;

    public PedidoService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public static BigDecimal calcularEnvio(BigDecimal subtotal) {
        if (subtotal.signum() == 0 || subtotal.compareTo(ENVIO_GRATIS_DESDE) >= 0) {
            return new BigDecimal("0.00");
        }
        return GASTOS_ENVIO;
    }

    /**
     * Convierte el carrito del usuario en un pedido, todo en una transaccion:
     * comprueba stock, descuenta stock, copia datos historicos, crea el pago
     * pendiente y vacia el carrito. Si algo falla no se guarda nada.
     */
    @Transactional
    public Map<String, Object> crear(PedidoRequest r) {

        Long carritoId = jdbc.sql("SELECT id FROM Carrito WHERE usuario_id = ?")
                .param(r.usuarioId())
                .query(Long.class)
                .optional()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // FOR UPDATE bloquea las filas para que dos compras simultaneas no vendan el mismo stock
        List<Map<String, Object>> items = jdbc.sql("""
                SELECT p.id, p.carta_externa_id, p.idioma, p.estado_carta, p.acabado,
                       p.precio, p.stock,
                       CASE WHEN p.activo THEN 1 ELSE 0 END AS activo,
                       d.cantidad
                FROM DetalleCarrito d
                JOIN Producto p ON p.id = d.producto_id
                WHERE d.carrito_id = ?
                ORDER BY p.id
                FOR UPDATE
                """)
                .param(carritoId)
                .query()
                .listOfRows();

        if (items.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El carrito esta vacio");
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        for (Map<String, Object> it : items) {
            int cantidad = ((Number) it.get("cantidad")).intValue();
            int stock = ((Number) it.get("stock")).intValue();
            boolean activo = ((Number) it.get("activo")).intValue() == 1;

            if (!activo || stock < cantidad) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "Stock insuficiente o producto no disponible: " + it.get("carta_externa_id"));
            }

            BigDecimal precio = new BigDecimal(it.get("precio").toString());
            subtotal = subtotal.add(precio.multiply(BigDecimal.valueOf(cantidad)));
        }

        BigDecimal envio = PedidoService.calcularEnvio(subtotal);
        BigDecimal total = subtotal.add(envio);

        // ---- Pedido (se copian los datos de envio para conservar el historial) ----
        KeyHolder pedidoKey = new GeneratedKeyHolder();

        jdbc.sql("""
                INSERT INTO Pedido
                (comprador_id, nombre_destinatario, calle, numero, piso, ciudad, provincia,
                 codigo_postal, pais, subtotal, gastos_envio, total)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """)
                .params(
                        r.usuarioId(),
                        r.nombreDestinatario().trim(),
                        r.calle().trim(),
                        r.numero().trim(),
                        blankToNull(r.piso()),
                        r.ciudad().trim(),
                        r.provincia().trim(),
                        r.codigoPostal().trim(),
                        r.pais().trim(),
                        subtotal,
                        envio,
                        total
                )
                .update(pedidoKey);

        long pedidoId = pedidoKey.getKey().longValue();

        // ---- Lineas + descuento de stock ----
        for (Map<String, Object> it : items) {

            String cartaId = (String) it.get("carta_externa_id");
            int cantidad = ((Number) it.get("cantidad")).intValue();
            long productoId = ((Number) it.get("id")).longValue();
            BigDecimal precio = new BigDecimal(it.get("precio").toString());

            PedidoRequest.DatosCarta datos = r.cartas() == null ? null : r.cartas().get(cartaId);
            String nombre = (datos != null && datos.nombre() != null && !datos.nombre().isBlank())
                    ? datos.nombre()
                    : cartaId;

            jdbc.sql("""
                    INSERT INTO DetallePedido
                    (pedido_id, producto_id, carta_externa_id, nombre_carta, nombre_set, numero_carta,
                     idioma, estado_carta, acabado, cantidad, precio_unitario, subtotal)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """)
                    .params(
                            pedidoId,
                            productoId,
                            cartaId,
                            cortar(nombre, 150),
                            datos == null ? null : cortar(datos.set(), 150),
                            datos == null ? null : cortar(datos.numero(), 30),
                            it.get("idioma"),
                            it.get("estado_carta").toString(),
                            it.get("acabado").toString(),
                            cantidad,
                            precio,
                            precio.multiply(BigDecimal.valueOf(cantidad))
                    )
                    .update();

            int filas = jdbc.sql("UPDATE Producto SET stock = stock - ? WHERE id = ? AND stock >= ?")
                    .params(cantidad, productoId, cantidad)
                    .update();

            if (filas != 1) {
                throw new ApiException(HttpStatus.CONFLICT, "Stock insuficiente: " + cartaId);
            }
        }

        // ---- Pago pendiente (no hay pasarela real: queda registrado como PENDIENTE) ----
        String referencia = "PED" + pedidoId + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        jdbc.sql("INSERT INTO Pago(pedido_id, metodo, importe, referencia_pago) VALUES (?, ?, ?, ?)")
                .params(pedidoId, r.metodoPago(), total, referencia)
                .update();

        // ---- Vaciar carrito ----
        jdbc.sql("DELETE FROM DetalleCarrito WHERE carrito_id = ?")
                .param(carritoId)
                .update();

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("pedidoId", pedidoId);
        resultado.put("estado", "PENDIENTE");
        resultado.put("subtotal", subtotal);
        resultado.put("gastosEnvio", envio);
        resultado.put("total", total);
        resultado.put("referenciaPago", referencia);
        return resultado;
    }

    public List<Map<String, Object>> listarPorUsuario(long usuarioId) {

        List<Map<String, Object>> pedidos = jdbc.sql("""
                SELECT id, fecha_pedido, estado, subtotal, gastos_envio, total,
                       (SELECT pg.estado FROM Pago pg
                         WHERE pg.pedido_id = Pedido.id
                         ORDER BY pg.id DESC LIMIT 1) AS pago_estado
                FROM Pedido
                WHERE comprador_id = ?
                ORDER BY id DESC
                """)
                .param(usuarioId)
                .query()
                .listOfRows();

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (Map<String, Object> p : pedidos) {
            Map<String, Object> copia = new LinkedHashMap<>(p);
            copia.put("lineas", jdbc.sql("""
                    SELECT nombre_carta, nombre_set, numero_carta, idioma, estado_carta,
                           acabado, cantidad, precio_unitario, subtotal
                    FROM DetallePedido
                    WHERE pedido_id = ?
                    ORDER BY id
                    """)
                    .param(p.get("id"))
                    .query()
                    .listOfRows());
            resultado.add(copia);
        }

        return resultado;
    }

    private String blankToNull(String v) {
        return v == null || v.isBlank() ? null : v.trim();
    }

    private String cortar(String v, int max) {
        if (v == null) {
            return null;
        }
        return v.length() <= max ? v : v.substring(0, max);
    }
}
