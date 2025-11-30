package com.yahveh.repository;

import com.yahveh.dto.NotaEntregaReporteDTO;
import com.yahveh.dto.response.NotaEntregaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.NotaEntrega;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
     * Obtener datos completos para el reporte
     */
    public NotaEntregaReporteDTO obtenerDatosReporte(long codNotaEntrega) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_codnotaentrega := ?, p_accion := 'R')";

        List<Map<String, Object>> resultados = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, codNotaEntrega);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();

                    // Encabezado
                    row.put("codNotaEntrega", rs.getLong("cod_nota_entrega"));
                    row.put("fecha", rs.getDate("fecha").toLocalDate());
                    row.put("codCliente", rs.getLong("cod_cliente"));
                    row.put("nombreCliente", rs.getString("nombre_cliente"));
                    row.put("nit", rs.getString("nit"));
                    row.put("razonSocial", rs.getString("razon_social"));
                    row.put("direccion", rs.getString("direccion"));
                    row.put("zona", rs.getString("zona"));
                    row.put("telefonos", rs.getString("telefonos"));
                    row.put("totalGeneral", rs.getFloat("total_general"));
                    row.put("totalSinFactura", rs.getFloat("total_sin_factura"));
                    row.put("totalArticulos", rs.getInt("total_articulos"));

                    // Detalle
                    row.put("codArticulo", rs.getString("cod_articulo"));
                    row.put("lineaArticulo", rs.getString("linea_articulo"));
                    row.put("descripcionArticulo", rs.getString("descripcion_articulo"));
                    row.put("cantidad", rs.getInt("cantidad"));
                    row.put("precioUnitario", rs.getFloat("precio_unitario"));
                    row.put("precioTotal", rs.getFloat("precio_total"));
                    row.put("precioSinFactura", rs.getFloat("precio_sin_factura"));
                    row.put("subtotalSinFactura", rs.getFloat("subtotal_sin_factura"));

                    resultados.add(row);
                }
            }
        } catch (SQLException e) {
            log.error("Error al obtener datos del reporte", e);
            throw new RuntimeException("Error al obtener datos del reporte", e);
        }

        if (resultados.isEmpty()) {
            throw new NotFoundException("Nota de entrega no encontrada");
        }

        // Construir el DTO
        Map<String, Object> primerRegistro = resultados.get(0);

        List<NotaEntregaReporteDTO.DetalleArticuloDTO> detalles = resultados.stream()
                .filter(row -> row.get("codArticulo") != null)
                .map(row -> NotaEntregaReporteDTO.DetalleArticuloDTO.builder()
                        .codArticulo((String) row.get("codArticulo"))
                        .lineaArticulo((String) row.get("lineaArticulo"))
                        .descripcionArticulo((String) row.get("descripcionArticulo"))
                        .cantidad((Integer) row.get("cantidad"))
                        .precioUnitario((Float) row.get("precioUnitario"))
                        .precioTotal((Float) row.get("precioTotal"))
                        .precioSinFactura((Float) row.get("precioSinFactura"))
                        .subtotalSinFactura((Float) row.get("subtotalSinFactura"))
                        .build())
                .collect(Collectors.toList());

        return NotaEntregaReporteDTO.builder()
                .codNotaEntrega((Long) primerRegistro.get("codNotaEntrega"))
                .fecha((LocalDate) primerRegistro.get("fecha"))
                .codCliente((Long) primerRegistro.get("codCliente"))
                .nombreCliente((String) primerRegistro.get("nombreCliente"))
                .nit((String) primerRegistro.get("nit"))
                .razonSocial((String) primerRegistro.get("razonSocial"))
                .direccion((String) primerRegistro.get("direccion"))
                .zona((String) primerRegistro.get("zona"))
                .telefonos((String) primerRegistro.get("telefonos"))
                .totalConFactura((Float) primerRegistro.get("totalGeneral"))
                .totalSinFactura((Float) primerRegistro.get("totalSinFactura"))
                .totalArticulos((Integer) primerRegistro.get("totalArticulos"))
                .detalles(detalles)
                .build();
    }

    /**
     * Mapear ResultSet a NotaEntregaResponse
     */
    private NotaEntregaResponse mapNotaEntregaResponse(ResultSet rs) throws SQLException {
        NotaEntregaResponse response = NotaEntregaResponse.builder()
                .codNotaEntrega(rs.getInt(1))        // cod_nota_entrega
                .codCliente(rs.getInt(2))            // cod_cliente
                .nombreCliente(rs.getString(3))      // nombre_cliente
                .fecha(rs.getDate(4).toLocalDate())  // fecha
                .direccion(rs.getString(5))          // direccion
                .zona(rs.getString(6))               // zona
                .audUsuario(rs.getInt(7))            // aud_usuario
                .audFecha(rs.getTimestamp(8).toLocalDateTime())  // aud_fecha
                .totalGeneral(rs.getFloat(9))       // total_general
                .totalArticulos(rs.getInt(10))      // total_articulos
                .build();

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