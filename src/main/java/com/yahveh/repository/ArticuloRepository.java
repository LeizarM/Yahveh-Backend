package com.yahveh.repository;

import com.yahveh.dto.response.ArticuloResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Articulo;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ArticuloRepository extends BaseRepository<Articulo> {

    /**
     * Resultado de operación ABM
     */
    public static class AbmResult {
        public int error;
        public String errorMsg;
        public int result;

        public boolean isSuccess() {
            return error == 0;
        }
    }

    /**
     * Listar todos los artículos con información completa
     */
    public List<ArticuloResponse> listarTodosCompleto() {
        String sql = "SELECT * " +
                "FROM p_list_articulo(p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, "L");
    }

    /**
     * Buscar artículo por ID con información completa
     */
    public Optional<ArticuloResponse> buscarPorIdCompleto(String codArticulo) {
        String sql = "SELECT * " +
                "FROM p_list_articulo(p_codarticulo := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapArticuloResponse, codArticulo, "L");
    }

    /**
     * Listar artículos por línea con información completa
     */
    public List<ArticuloResponse> listarPorLineaCompleto(int codLinea) {
        String sql = "SELECT * " +
                "FROM p_list_articulo(p_codlinea := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, codLinea, "L");
    }

    /**
     * Listar artículos por familia con información completa
     */
    public List<ArticuloResponse> listarPorFamiliaCompleto(int codFamilia) {
        String sql = "SELECT * " +
                "FROM p_list_articulo(p_codfamilia := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, codFamilia, "L");
    }

    /**
     * Buscar artículos por nombre con información completa
     */
    public List<ArticuloResponse> buscarPorNombreCompleto(String articulo) {
        String sql = "SELECT * " +
                "FROM p_list_articulo(p_articulo := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, articulo, "L");
    }

    /**
     * Crear nuevo artículo con manejo de errores
     */
    public int crearArticulo(Articulo articulo) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_articulo(" +
                "p_codarticulo := ?, " +
                "p_codlinea := ?, " +
                "p_descripcion := ?, " +
                "p_descripcion2 := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                articulo.getCodArticulo(),
                articulo.getCodLinea(),
                articulo.getDescripcion(),
                articulo.getDescripcion2(),
                articulo.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al crear artículo. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Artículo creado exitosamente con ID: {}", result.result);
        return result.result;
    }

    /**
     * Actualizar artículo con manejo de errores
     */
    public void actualizarArticulo(Articulo articulo) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_articulo(" +
                "p_codarticulo := ?, " +
                "p_codlinea := ?, " +
                "p_descripcion := ?, " +
                "p_descripcion2 := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                articulo.getCodArticulo(),
                articulo.getCodLinea(),
                articulo.getDescripcion(),
                articulo.getDescripcion2(),
                articulo.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar artículo. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Artículo actualizado exitosamente: {}", articulo.getCodArticulo());
    }

    /**
     * Eliminar artículo con manejo de errores
     */
    public void eliminarArticulo(String codArticulo, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_articulo(" +
                "p_codarticulo := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codArticulo,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar artículo. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Artículo eliminado exitosamente: {}", codArticulo);
    }

    /**
     * Mapear ResultSet a ArticuloResponse
     */
    private ArticuloResponse mapArticuloResponse(ResultSet rs) throws SQLException {
        return ArticuloResponse.builder()
                .codArticulo(rs.getString(1))
                .codLinea(rs.getInt(2))
                .linea(rs.getString(3))
                .descripcion(rs.getString(4))
                .descripcion2(rs.getString(5))
                .stockActual(rs.getInt(6))
                .precioActual(rs.getDouble(7))
                .audUsuario(rs.getInt(8))
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
        result.result = rs.wasNull() ? null : resultValue;

        return result;
    }
}