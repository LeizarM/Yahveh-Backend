package com.yahveh.repository;

import com.yahveh.dto.response.ArticuloResponse;
import com.yahveh.dto.response.PagedResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Articulo;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
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
        public String result;  // Cambiado de int a String

        public boolean isSuccess() {
            return error == 0;
        }
    }

    /**
     * Listar todos los artículos con información completa (sin paginación, backwards compat).
     */
    public List<ArticuloResponse> listarTodosCompleto() {
        String sql = "SELECT * " +
                "FROM p_list_articulo(p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, "L");
    }

    /**
     * ⭐ Listar artículos paginados desde la BD (server-side pagination).
     *
     * El SP retorna en cada fila el total_records (mismo valor en todas las filas)
     * para que con una sola consulta tengamos data + total, evitando un COUNT(*) extra.
     *
     * Si la página queda vacía (filtro sin matches), hacemos un fallback rápido
     * para obtener el total real.
     */
    public PagedResponse<ArticuloResponse> listarPaginado(String search, int page, int pageSize) {
        // Normalización de parámetros
        if (page < 1) page = 1;
        if (pageSize < 1) pageSize = 20;
        if (pageSize > 500) pageSize = 500;

        int offset = (page - 1) * pageSize;

        String sql = "SELECT * FROM p_list_articulo(" +
                "p_codarticulo := NULL, " +
                "p_codlinea := NULL, " +
                "p_descripcion := NULL, " +
                "p_audusuario := NULL, " +
                "p_accion := 'L', " +
                "p_offset := ?, " +
                "p_limit := ?, " +
                "p_search := ?" +
                ")";

        List<ArticuloResponse> data = new ArrayList<>();
        long total = 0;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, offset);
            stmt.setInt(2, pageSize);
            if (search == null || search.isBlank()) {
                stmt.setNull(3, Types.VARCHAR);
            } else {
                stmt.setString(3, search.trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ArticuloResponse a = mapArticuloResponse(rs);
                    long t = rs.getLong("total_records");
                    if (!rs.wasNull()) total = t;
                    data.add(a);
                }
            }
        } catch (SQLException e) {
            log.error("Error al listar artículos paginados", e);
            throw new RuntimeException("Error al listar artículos paginados: " + e.getMessage(), e);
        }

        // Si la página actual está vacía pero existen registros previos,
        // pedimos el total con un offset 0 / limit 1 para no perder el contador.
        if (data.isEmpty() && offset > 0) {
            total = obtenerTotal(search);
        }

        return PagedResponse.of(data, total, page, pageSize);
    }

    /**
     * Obtiene solo el total de artículos que matchean un search dado.
     * Se usa como fallback cuando la página actual está vacía.
     */
    private long obtenerTotal(String search) {
        String sql = "SELECT total_records FROM p_list_articulo(" +
                "p_accion := 'L', p_offset := 0, p_limit := 1, p_search := ?) LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (search == null || search.isBlank()) {
                stmt.setNull(1, Types.VARCHAR);
            } else {
                stmt.setString(1, search.trim());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            log.warn("Fallback de total falló: {}", e.getMessage());
        }
        return 0L;
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
    public String crearArticulo(Articulo articulo) {  // Retorna String ahora
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
                articulo.getCodArticulo(),  // Puede ser NULL para auto-generar
                articulo.getCodLinea(),
                articulo.getDescripcion(),
                articulo.getDescripcion2(),
                articulo.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al crear artículo. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Artículo creado exitosamente con código: {}", result.result);
        return result.result;  // Retorna el código generado
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
     * Mapear ResultSet a ArticuloResponse. Usa nombres de columna en vez de
     * índices para tolerar columnas extra (total_records, row_number).
     */
    private ArticuloResponse mapArticuloResponse(ResultSet rs) throws SQLException {
        return ArticuloResponse.builder()
                .codArticulo(rs.getString("cod_articulo"))
                .codLinea(rs.getInt("cod_linea"))
                .linea(rs.getString("linea"))
                .descripcion(rs.getString("descripcion"))
                .descripcion2(rs.getString("descripcion2"))
                .stockActual(rs.getInt("total_stock"))
                .precioActual(rs.getDouble("precio_actual"))
                .precioSinFactura(safeGetDouble(rs, "precio_sin_factura"))
                .audUsuario(rs.getInt("aud_usuario"))
                .rowNumber(safeGetLong(rs, "row_number"))
                .build();
    }

    private long safeGetLong(ResultSet rs, String column) {
        try {
            long val = rs.getLong(column);
            return rs.wasNull() ? 0L : val;
        } catch (SQLException e) {
            return 0L;
        }
    }

    private double safeGetDouble(ResultSet rs, String column) {
        try {
            double val = rs.getDouble(column);
            return rs.wasNull() ? 0.0 : val;
        } catch (SQLException e) {
            return 0.0;
        }
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");
        result.result = rs.getString("p_result");  // Ahora es String
        return result;
    }
}