package com.yahveh.repository;


import com.yahveh.model.Vista;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@ApplicationScoped
public class VistaRepository extends BaseRepository<Vista> {

    /**
     * Listar todas las vistas (menú)
     */
    public List<Vista> listarTodas() {
        String sql = "SELECT codvista, codvistapadre, direccion, titulo, audusuario " +
                "FROM p_list_vista(p_accion := ?)";
        return executeQueryList(sql, this::mapVista, "L");
    }

    private Vista mapVista(ResultSet rs) throws SQLException {
        return Vista.builder()
                .codVista(rs.getLong(1))
                .codVistaPadre(rs.getLong(2))
                .direccion(rs.getString(3))
                .titulo(rs.getString(4))
                .audUsuario(rs.getLong(5))
                .build();
    }
}
