package com.yahveh.repository;


import com.yahveh.dto.response.InventarioResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Inventario;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class InventarioRepository extends BaseRepository<Inventario> {

    /**

    /**
     * Listar todos los movimientos de inventario
     */
    public List<InventarioResponse> listarTodos() {
        String sql = "SELECT * FROM p_list_inventario()";
        return executeQueryList(sql, this::mapInventarioResponse);
    }

    /**
     * Buscar movimiento por código
     */
    public Optional<InventarioResponse> buscarPorCodigo(int codInventario) {
        String sql = "SELECT * FROM p_list_inventario(p_codinventario := ?)";
        return executeQuerySingle(sql, this::mapInventarioResponse, codInventario);
    }

    /**
     * Listar movimientos por artículo
     */
    public List<InventarioResponse> listarPorArticulo(String codArticulo) {
        String sql = "SELECT * FROM p_list_inventario(p_codarticulo := ?)";
        return executeQueryList(sql, this::mapInventarioResponse, codArticulo);
    }

    /**
     * Listar movimientos por tipo
     */
    public List<InventarioResponse> listarPorTipo(String tipoMovimiento) {
        String sql = "SELECT * FROM p_list_inventario(p_tipomovimiento := ?)";
        return executeQueryList(sql, this::mapInventarioResponse, tipoMovimiento);
    }

    /**
     * Listar movimientos por rango de fechas
     */
    public List<InventarioResponse> listarPorFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        String sql = "SELECT * FROM p_list_inventario(p_fecha_desde := ?, p_fecha_hasta := ?)";
        return executeQueryList(sql, this::mapInventarioResponse, fechaDesde, fechaHasta);
    }

    /**
     * Crear nuevo movimiento de inventario
     */
    public long crearMovimiento(String codArticulo, String tipoMovimiento, int cantidad,
                                float precioUnitario, LocalDate fecha, String observacion, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_inventario(" +
                "p_codarticulo := ?, " +
                "p_tipomovimiento := ?, " +
                "p_cantidad := ?, " +
                "p_preciounitario := ?, " +
                "p_fecha := ?, " +
                "p_observacion := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codArticulo,
                tipoMovimiento,
                cantidad,
                precioUnitario,
                fecha,
                observacion,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear movimiento de inventario. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Modificar observación de un movimiento
     */
    public void modificarObservacion(int codInventario, String observacion, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_inventario(" +
                "p_codinventario := ?, " +
                "p_observacion := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codInventario,
                observacion,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al modificar movimiento de inventario. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar (reversar) movimiento de inventario
     */
    public void eliminarMovimiento(int codInventario, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_inventario(" +
                "p_codinventario := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codInventario,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar movimiento de inventario. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Mapear ResultSet a InventarioResponse
     */
    private InventarioResponse mapInventarioResponse(ResultSet rs) throws SQLException {
        InventarioResponse response = new InventarioResponse();
        response.setCodInventario(rs.getInt(1));
        response.setCodArticulo(rs.getString(2));
        response.setDescripcionArticulo(rs.getString(3));
        response.setDescripcion2Articulo(rs.getString(4));
        response.setLineaArticulo(rs.getString(5));
        response.setTipoMovimiento(rs.getString(6));
        response.setCantidad(rs.getInt(7));
        response.setSaldoAnterior(rs.getInt(8));
        response.setSaldoNuevo(rs.getInt(9));
        response.setPrecioUnitario(rs.getFloat(10));
        response.setValorTotal(rs.getFloat(11));

        // Manejar fecha
        java.sql.Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            response.setFecha(fecha.toLocalDate());
        }

        response.setObservacion(rs.getString("observacion"));
        response.setAudUsuario(rs.getInt("aud_usuario"));



        return response;
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        // Manejar el caso donde p_result puede ser NULL
        long resultValue = rs.getLong("p_result");
        result.result = rs.wasNull() ? null : (int) resultValue;

        return result;
    }
}