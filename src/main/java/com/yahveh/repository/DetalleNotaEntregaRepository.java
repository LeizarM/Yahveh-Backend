package com.yahveh.repository;

import com.yahveh.dto.response.DetalleNotaEntregaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.DetalleNotaEntrega;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class DetalleNotaEntregaRepository extends BaseRepository<DetalleNotaEntrega> {

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
     * Listar detalles por nota de entrega
     */
    public List<DetalleNotaEntregaResponse> listarPorNotaEntrega(int codNotaEntrega) {
        String sql = "SELECT * FROM p_list_detalle_nota_entrega(p_codnotaentrega := ?)";
        return executeQueryList(sql, this::mapDetalleResponse, codNotaEntrega);
    }

    /**
     * Buscar detalle por código
     */
    public Optional<DetalleNotaEntregaResponse> buscarPorCodigo(int codDetalle) {
        String sql = "SELECT * FROM p_list_detalle_nota_entrega(p_coddetalle := ?)";
        return executeQuerySingle(sql, this::mapDetalleResponse, codDetalle);
    }

    /**
     * Crear nuevo detalle
     */
    public int crearDetalle(int codNotaEntrega, String codArticulo, int cantidad,
                             BigDecimal precioUnitario, BigDecimal precioSinFactura, float descuento, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_detalle_nota_entrega(" +
                "p_codnotaentrega := ?, " +
                "p_codarticulo := ?, " +
                "p_cantidad := ?, " +
                "p_preciounitario := ?, " +
                "p_preciosinfactura := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I', " +
                "p_descuento := ?)";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codNotaEntrega,
                codArticulo,
                cantidad,
                precioUnitario,
                precioSinFactura,
                audUsuario,
                descuento
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear detalle. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar detalle
     */
    public void actualizarDetalle(int codDetalle, int cantidad, BigDecimal precioUnitario,
                                  BigDecimal precioSinFactura, float descuento, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_detalle_nota_entrega(" +
                "p_coddetalle := ?, " +
                "p_cantidad := ?, " +
                "p_preciounitario := ?, " +
                "p_preciosinfactura := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U', " +
                "p_descuento := ?)";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codDetalle,
                cantidad,
                precioUnitario,
                precioSinFactura,
                audUsuario,
                descuento
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar detalle. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar detalle
     */
    public void eliminarDetalle(int codDetalle, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_detalle_nota_entrega(" +
                "p_coddetalle := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codDetalle,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar detalle. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    private DetalleNotaEntregaResponse mapDetalleResponse(ResultSet rs) throws SQLException {
        // ⭐ Usar nombres de columnas (más robusto que índices) y agregar precio_con_descuento
        return DetalleNotaEntregaResponse.builder()
                .codDetalle(rs.getInt("cod_detalle"))
                .codNotaEntrega(rs.getInt("cod_nota_entrega"))
                .codArticulo(rs.getString("cod_articulo"))
                .descripcionArticulo(rs.getString("descripcion_articulo"))
                .descripcion2Articulo(rs.getString("descripcion2_articulo"))
                .lineaArticulo(rs.getString("linea_articulo"))
                .codLinea(rs.getInt("cod_linea"))
                .cantidad(rs.getInt("cantidad"))
                .precioUnitario(safeGetBigDecimal(rs, "precio_unitario"))
                .precioTotal(safeGetBigDecimal(rs, "precio_total"))
                .precioSinFactura(safeGetBigDecimal(rs, "precio_sin_factura"))
                .descuento(safeGetFloat(rs, "descuento"))
                .precioConDescuento(safeGetBigDecimal(rs, "precio_con_descuento"))
                .audUsuario(rs.getInt("aud_usuario"))
                .build();
    }

    private float safeGetFloat(ResultSet rs, String column) {
        try {
            float val = rs.getFloat(column);
            return rs.wasNull() ? 0f : val;
        } catch (SQLException e) {
            return 0f;
        }
    }

    private BigDecimal safeGetBigDecimal(ResultSet rs, String column) {
        try {
            BigDecimal val = rs.getBigDecimal(column);
            return val != null ? val : BigDecimal.ZERO;
        } catch (SQLException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        int resultValue = rs.getInt("p_result");
        result.result = rs.wasNull() ? null : resultValue;

        return result;
    }
}