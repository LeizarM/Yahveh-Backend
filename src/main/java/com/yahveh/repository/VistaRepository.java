package com.yahveh.repository;


import com.yahveh.exception.BusinessException;
import com.yahveh.model.Vista;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.List;

@Slf4j
@ApplicationScoped
public class VistaRepository extends BaseRepository<Vista> {

    /**
     * Listar vistas permitidas para un usuario (menú dinámico)
     */
    public List<Vista> listarTodas(long codUsuario) {
        String sql = "SELECT codvista, codvistapadre, direccion, titulo, audusuario " +
                "FROM p_list_vista(p_codusuario := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapVista, codUsuario, "L");
    }

    /**
     * Listar TODAS las vistas del sistema (sin filtro de usuario).
     * p_list_vista sin p_codusuario devuelve todo (IF p_codusuario IS NULL).
     * Usado por el panel de administración de permisos.
     */
    public List<Vista> listarTodasAdmin() {
        String sql = "SELECT codvista, codvistapadre, direccion, titulo, audusuario " +
                "FROM p_list_vista(p_accion := ?)";
        return executeQueryList(sql, this::mapVista, "L");
    }

    /**
     * Listar vistas asignadas a un usuario específico (respeta tipoUsuario).
     * - admin  → devuelve todas (la SP lo maneja así)
     * - lim    → devuelve solo las filas en tb_usuario_vista
     */
    public List<Vista> listarDeUsuario(long codUsuario) {
        String sql = "SELECT codvista, codvistapadre, direccion, titulo, audusuario " +
                "FROM p_list_vista(p_codusuario := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapVista, codUsuario, "L");
    }

    /**
     * Reemplazar (bulk-set) todas las vistas de un usuario.
     * Borra las asignaciones anteriores e inserta las nuevas.
     */
    public void actualizarVistasDeUsuario(long codUsuario, List<Long> codVistas) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_usuario_vista(" +
                "p_codusuario := ?::BIGINT, " +
                "p_codvistas  := ?::BIGINT[], " +
                "p_accion     := 'S')";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, codUsuario);

            // Construir el array BIGINT[] de PostgreSQL
            Long[] arr = codVistas != null ? codVistas.toArray(new Long[0]) : new Long[0];
            Array pgArray = conn.createArrayOf("int8", arr);
            stmt.setArray(2, pgArray);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int error = rs.getInt("p_error");
                    if (error != 0) {
                        String msg = rs.getString("p_errormsg");
                        log.error("Error al actualizar vistas de usuario {}: {}", codUsuario, msg);
                        throw new BusinessException(msg);
                    }
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (SQLException e) {
            log.error("Error SQL al actualizar vistas de usuario {}", codUsuario, e);
            throw new RuntimeException("Error al actualizar permisos del usuario", e);
        }
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
