package com.cardtrade.service;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class FavoritoService {

    private final JdbcClient jdbc;

    public FavoritoService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Devuelve los ids externos de las cartas favoritas del usuario. */
    public List<String> listar(long usuarioId) {
        return jdbc.sql("""
                SELECT carta_externa_id
                FROM Favorito
                WHERE usuario_id = ?
                ORDER BY fecha_agregado DESC
                """)
                .param(usuarioId)
                .query(String.class)
                .list();
    }

    public List<String> anadir(long usuarioId, String cartaExternaId) {
        jdbc.sql("""
                INSERT INTO Favorito(usuario_id, carta_externa_id)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE fecha_agregado = fecha_agregado
                """)
                .params(usuarioId, cartaExternaId)
                .update();
        return listar(usuarioId);
    }

    public List<String> quitar(long usuarioId, String cartaExternaId) {
        jdbc.sql("DELETE FROM Favorito WHERE usuario_id = ? AND carta_externa_id = ?")
                .params(usuarioId, cartaExternaId)
                .update();
        return listar(usuarioId);
    }
}
