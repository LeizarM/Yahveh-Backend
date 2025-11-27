package com.yahveh.repository;

import com.yahveh.dto.response.LineaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Linea;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class LineaRepository extends BaseRepository<Linea> {

    /**

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
     * Crear nueva línea con manejo de errores
     */
    public int crearLinea(Linea linea) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_linea(" +
                "p_codfamilia := ?, " +
                "p_linea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                linea.getCodFamilia(),
                linea.getLinea(),
                linea.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear línea. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar línea con manejo de errores
     */
    public void actualizarLinea(Linea linea) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_linea(" +
                "p_codlinea := ?, " +
                "p_codfamilia := ?, " +
                "p_linea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                linea.getCodLinea(),
                linea.getCodFamilia(),
                linea.getLinea(),
                linea.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar línea. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar línea con manejo de errores
     */
    public void eliminarLinea(int codLinea, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_linea(" +
                "p_codlinea := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codLinea,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar línea. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
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
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        // Manejar el caso donde p_result puede ser NULL
        int resultValue = rs.getInt("p_result");
        result.result = rs.wasNull() ? null : Integer.valueOf(resultValue);

        return result;
    }
}