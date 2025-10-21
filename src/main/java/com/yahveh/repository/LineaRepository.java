package com.yahveh.repository;

import com.yahveh.model.Linea;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LineaRepository extends BaseRepository<Linea> {

    /**
     * Listar todas las líneas
     */
    public List<Linea> listarTodas() {
        String sql = "SELECT cod_linea, linea, total_articulos, articulos_activos, aud_usuario " +
                "FROM p_list_linea(p_accion := ?)";
        return executeQueryList(sql, this::mapLinea, "L");
    }

    /**
     * Buscar línea por ID
     */
    public Optional<Linea> buscarPorId(int codLinea) {
        String sql = "SELECT cod_linea, linea, total_articulos, articulos_activos, aud_usuario " +
                "FROM p_list_linea(p_codlinea := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapLinea, codLinea, "L");
    }

    /**
     * Buscar línea por nombre
     */
    public List<Linea> buscarPorNombre(String linea) {
        String sql = "SELECT cod_linea, linea, total_articulos, articulos_activos, aud_usuario " +
                "FROM p_list_linea(p_linea := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapLinea, linea, "L");
    }

    /**
     * Crear nueva línea
     */
    public Long crearLinea(Linea linea) {
        String sql = "SELECT p_abm_linea(" +
                "p_linea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                linea.getLinea(),
                linea.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al crear línea"));
    }

    /**
     * Actualizar línea
     */
    public void actualizarLinea(Linea linea) {
        String sql = "SELECT p_abm_linea(" +
                "p_codlinea := ?, " +
                "p_linea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        // CAMBIO: Usar executeQuerySingle en lugar de executeUpdate
        executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                linea.getCodLinea(),
                linea.getLinea(),
                linea.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar línea"));
    }

    /**
     * Eliminar línea
     */
    public void eliminarLinea(int codLinea, Long audUsuario) {
        String sql = "SELECT p_abm_linea(" +
                "p_codlinea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        // CAMBIO: Usar executeQuerySingle en lugar de executeUpdate
        executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                codLinea,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar línea"));
    }

    /**
     * Mapear ResultSet a Linea
     */
    private Linea mapLinea(ResultSet rs) throws SQLException {
        return Linea.builder()
                .codLinea(rs.getInt(1))
                .linea(rs.getString(2))
                .audUsuario(rs.getLong(3))
                .build();
    }
}