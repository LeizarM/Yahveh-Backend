package com.yahveh.repository;

import com.yahveh.dto.response.LineaResponse;
import com.yahveh.model.Linea;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LineaRepository extends BaseRepository<Linea> {

    /**
     * Listar todas las líneas con información completa
     */
    public List<LineaResponse> listarTodasCompleto() {
        String sql = "SELECT cod_linea, cod_familia, familia, linea, total_articulos, aud_usuario " +
                "FROM p_list_linea(p_accion := ?)";
        return executeQueryList(sql, this::mapLineaResponse, "L");
    }

    /**
     * Buscar línea por ID con información completa
     */
    public Optional<LineaResponse> buscarPorIdCompleto(int codLinea) {
        String sql = "SELECT cod_linea, cod_familia, familia, linea, total_articulos, aud_usuario " +
                "FROM p_list_linea(p_codlinea := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapLineaResponse, codLinea, "L");
    }

    /**
     * Listar líneas por familia con información completa
     */
    public List<LineaResponse> listarPorFamiliaCompleto(int codFamilia) {
        String sql = "SELECT cod_linea, cod_familia, familia, linea, total_articulos, aud_usuario " +
                "FROM p_list_linea(p_codfamilia := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapLineaResponse, codFamilia, "L");
    }

    /**
     * Buscar líneas por nombre con información completa
     */
    public List<LineaResponse> buscarPorNombreCompleto(String linea) {
        String sql = "SELECT cod_linea, cod_familia, familia, linea, total_articulos, aud_usuario " +
                "FROM p_list_linea(p_linea := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapLineaResponse, linea, "L");
    }

    /**
     * Buscar línea por ID (solo para validaciones)
     */
    public Optional<Linea> buscarPorId(int codLinea) {
        String sql = "SELECT cod_linea, cod_familia, familia, linea, total_articulos, aud_usuario " +
                "FROM p_list_linea(p_codlinea := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapLinea, codLinea, "L");
    }

    /**
     * Crear nueva línea
     */
    public int crearLinea(Linea linea) {
        String sql = "SELECT p_abm_linea(" +
                "p_codfamilia := ?, " +
                "p_linea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                linea.getCodFamilia(),
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
                "p_codfamilia := ?, " +
                "p_linea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                linea.getCodLinea(),
                linea.getCodFamilia(),
                linea.getLinea(),
                linea.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar línea"));
    }

    /**
     * Eliminar línea
     */
    public void eliminarLinea(int codLinea, int audUsuario) {
        String sql = "SELECT p_abm_linea(" +
                "p_codlinea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                codLinea,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar línea"));
    }

    /**
     * Mapear ResultSet a LineaResponse
     */
    private LineaResponse mapLineaResponse(ResultSet rs) throws SQLException {
        return LineaResponse.builder()
                .codLinea(rs.getInt(1))
                .codFamilia(rs.getLong(2))
                .familia(rs.getString(3))
                .linea(rs.getString(4))
                .totalArticulos(rs.getInt(5))
                .audUsuario(rs.getInt(6))
                .build();
    }

    /**
     * Mapear ResultSet a Linea
     */
    private Linea mapLinea(ResultSet rs) throws SQLException {
        return Linea.builder()
                .codLinea(rs.getInt(1))
                .codFamilia(rs.getLong(2))
                .linea(rs.getString(3))
                .audUsuario(rs.getLong(4))
                .build();
    }
}