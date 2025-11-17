package com.yahveh.repository;

import com.yahveh.dto.response.NotaEntregaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.NotaEntrega;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class NotaEntregaRepository extends BaseRepository<NotaEntrega> {

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
     * Listar todas las notas de entrega
     */
    public List<NotaEntregaResponse> listarTodas() {
        String sql = "SELECT * FROM p_list_nota_entrega()";
        return executeQueryList(sql, this::mapNotaEntregaResponse);
    }

    /**
     * Buscar nota de entrega por código
     */
    public Optional<NotaEntregaResponse> buscarPorCodigo(int codNotaEntrega) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_codnotaentrega := ?)";
        return executeQuerySingle(sql, this::mapNotaEntregaResponse, codNotaEntrega);
    }

    /**
     * Listar notas de entrega por cliente
     */
    public List<NotaEntregaResponse> listarPorCliente(int codCliente) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_codcliente := ?)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, codCliente);
    }

    /**
     * Listar notas de entrega por rango de fechas
     */
    public List<NotaEntregaResponse> listarPorFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_fecha_desde := ?, p_fecha_hasta := ?)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, fechaDesde, fechaHasta);
    }

    /**
     * Crear nueva nota de entrega
     */
    public int crearNotaEntrega(int codCliente, LocalDate fecha, String direccion,
                                 String zona, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_nota_entrega(" +
                "p_codcliente := ?, " +
                "p_fecha := ?, " +
                "p_direccion := ?, " +
                "p_zona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codCliente,
                fecha,
                direccion,
                zona,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear nota de entrega. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar nota de entrega
     */
    public void actualizarNotaEntrega(int codNotaEntrega, int codCliente, LocalDate fecha,
                                      String direccion, String zona, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_nota_entrega(" +
                "p_codnotaentrega := ?, " +
                "p_codcliente := ?, " +
                "p_fecha := ?, " +
                "p_direccion := ?, " +
                "p_zona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codNotaEntrega,
                codCliente,
                fecha,
                direccion,
                zona,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar nota de entrega. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar nota de entrega
     */
    public void eliminarNotaEntrega(int codNotaEntrega, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_nota_entrega(" +
                "p_codnotaentrega := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codNotaEntrega,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar nota de entrega. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Mapear ResultSet a NotaEntregaResponse
     */
    private NotaEntregaResponse mapNotaEntregaResponse(ResultSet rs) throws SQLException {
        NotaEntregaResponse response = NotaEntregaResponse.builder()
                .codNotaEntrega(rs.getInt(1))
                .codCliente(rs.getInt(2))
                .nombreCliente(rs.getString(3))
                .direccion(rs.getString(4))
                .zona(rs.getString(5))
                .audUsuario(rs.getInt(6))
                .totalGeneral(rs.getFloat(7))
                .totalArticulos(rs.getInt(8))
                .build();

        // Manejar fecha
        java.sql.Date fecha = rs.getDate("fecha");
        if (fecha != null) {
            response.setFecha(fecha.toLocalDate());
        }



        return response;
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