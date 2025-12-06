package com.yahveh.repository;

import com.yahveh.dto.NotaEntregaReporteDTO;
import com.yahveh.dto.VentaReporteDTO;
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
        public Integer result;

        public boolean isSuccess() {
            return error == 0;
        }
    }

    public List<NotaEntregaResponse> listarTodas() {
        String sql = "SELECT * FROM p_list_nota_entrega(p_estado := 1)";
        return executeQueryList(sql, this::mapNotaEntregaResponse);
    }


    /**
     * Listar todas las notas (válidas y anuladas)
     */
    public List<NotaEntregaResponse> listarTodasConAnuladas() {
        String sql = "SELECT * FROM p_list_nota_entrega()"; // sin filtros
        return executeQueryList(sql, this::mapNotaEntregaResponse);
    }


    /**
     * Listar solo notas anuladas
     */
    public List<NotaEntregaResponse> listarAnuladas() {
        String sql = "SELECT * FROM p_list_nota_entrega(p_estado := 0)";
        return executeQueryList(sql, this::mapNotaEntregaResponse);
    }


    /**
     * Buscar nota de entrega por código
     */
    public Optional<NotaEntregaResponse> buscarPorCodigo(long codNotaEntrega) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_codnotaentrega := ?)";
        return executeQuerySingle(sql, this::mapNotaEntregaResponse, codNotaEntrega);
    }

    /**
     * Listar notas de entrega por cliente (solo válidas)
     */
    public List<NotaEntregaResponse> listarPorCliente(long codCliente) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_codcliente := ?, p_estado := 1)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, codCliente);
    }

    /**
     * Listar notas de entrega por rango de fechas (solo válidas)
     */
    public List<NotaEntregaResponse> listarPorFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_fecha_desde := ?, p_fecha_hasta := ?, p_estado := 1)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, fechaDesde, fechaHasta);
    }

    /**
     * Crear nueva nota de entrega
     */
    public int crearNotaEntrega(long codCliente, LocalDate fecha,
                                String direccion, String zona, long audUsuario) {
        String sql = """
        SELECT p_error, p_errormsg, p_result 
        FROM p_abm_nota_entrega(
            p_codcliente := ?::BIGINT, 
            p_fecha := ?::DATE, 
            p_direccion := ?::VARCHAR, 
            p_zona := ?::VARCHAR, 
            p_audusuario := ?::BIGINT, 
            p_accion := 'I'::VARCHAR
        )
        """;

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codCliente, fecha, direccion, zona, audUsuario)
                .orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear nota de entrega. Código: {}, Mensaje: {}",
                    result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar nota de entrega
     */
    public void actualizarNotaEntrega(long codNotaEntrega, LocalDate fecha,
                                      String direccion, String zona, long audUsuario) {
        String sql = """
        SELECT p_error, p_errormsg, p_result 
        FROM p_abm_nota_entrega(
            p_codnotaentrega := ?::BIGINT, 
            p_fecha := ?::DATE, 
            p_direccion := ?::VARCHAR, 
            p_zona := ?::VARCHAR, 
            p_audusuario := ?::BIGINT, 
            p_accion := 'U'::VARCHAR
        )
        """;

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codNotaEntrega, fecha, direccion, zona, audUsuario)
                .orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar. Código: {}, Mensaje: {}",
                    result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Anular nota de entrega
     */
    public void anularNotaEntrega(long codNotaEntrega, long audUsuario) {
        String sql = """
        SELECT p_error, p_errormsg, p_result 
        FROM p_abm_nota_entrega(
            p_codnotaentrega := ?::BIGINT, 
            p_audusuario := ?::BIGINT, 
            p_accion := 'A'::VARCHAR
        )
        """;

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codNotaEntrega, audUsuario)
                .orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar nota de entrega
     */
    public void eliminarNotaEntrega(long codNotaEntrega, long audUsuario) {
        String sql = """
        SELECT p_error, p_errormsg, p_result 
        FROM p_abm_nota_entrega(
            p_codnotaentrega := ?::BIGINT, 
            p_audusuario := ?::BIGINT, 
            p_accion := 'D'::VARCHAR
        )
        """;

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codNotaEntrega, audUsuario)
                .orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
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
                    row.put("estado", rs.getInt("estado"));                    // ⭐ Nuevo
                    row.put("estadoTexto", rs.getString("estado_texto"));      // ⭐ Nuevo
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
                .estado((Integer) primerRegistro.get("estado"))                // ⭐ Nuevo
                .estadoTexto((String) primerRegistro.get("estadoTexto"))       // ⭐ Nuevo
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
        return NotaEntregaResponse.builder()
                .codNotaEntrega(rs.getInt("cod_nota_entrega"))
                .codCliente(rs.getInt("cod_cliente"))
                .nombreCliente(rs.getString("nombre_cliente"))
                .fecha(rs.getDate("fecha").toLocalDate())
                .direccion(rs.getString("direccion"))
                .zona(rs.getString("zona"))
                .audUsuario(rs.getInt("aud_usuario"))
                .audFecha(rs.getTimestamp("aud_fecha").toLocalDateTime())
                .estado(rs.getInt("estado"))                    // ⭐ Nuevo
                .estadoTexto(rs.getString("estado_texto"))      // ⭐ Nuevo
                .totalGeneral(rs.getFloat("total_general"))
                .totalArticulos(rs.getInt("total_articulos"))
                .build();
    }

    /**
     * ⭐ NUEVO: Obtener datos para reporte de ventas usando el SP
     */
    public List<VentaReporteDTO> obtenerReporteVentas(LocalDate fechaDesde, LocalDate fechaHasta) {
        String sql = """
        SELECT * FROM p_list_nota_entrega(
            p_accion := 'V'::VARCHAR,
            p_codnotaentrega := NULL::BIGINT,
            p_codcliente := NULL::BIGINT,
            p_fecha_desde := ?::DATE,
            p_fecha_hasta := ?::DATE,
            p_estado := 1::INTEGER
        )
        """;

        List<VentaReporteDTO> ventas = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fechaDesde));
            stmt.setDate(2, java.sql.Date.valueOf(fechaHasta));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Determinar si es fila de detalle o total
                    String estadoTexto = rs.getString("estado_texto");
                    String tipoFila = "TOTAL".equals(estadoTexto) ? "TOTAL" : "DETALLE";

                    VentaReporteDTO venta = VentaReporteDTO.builder()
                            .fecha(rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null)
                            .codCliente(rs.getObject("cod_cliente") != null ? rs.getLong("cod_cliente") : null)
                            .nombreCliente(rs.getString("nombre_cliente"))
                            .direccion(rs.getString("direccion"))
                            .ciudad(rs.getString("zona"))
                            .codArticulo(rs.getString("cod_articulo"))
                            .cantidad(rs.getObject("cantidad") != null ? rs.getInt("cantidad") : null)
                            .lineaArticulo(rs.getString("linea_articulo"))
                            .productoCompleto(rs.getString("producto_completo"))
                            .precioUnitario(rs.getObject("precio_unitario") != null ? rs.getFloat("precio_unitario") : null)
                            .descuento(rs.getObject("descuento") != null ? rs.getFloat("descuento") : null)
                            .totalBs(rs.getObject("total_bs") != null ? rs.getFloat("total_bs") : null)
                            .descBs(rs.getObject("desc_bs") != null ? rs.getFloat("desc_bs") : null)
                            .bsUnitario(rs.getObject("bs_unitario") != null ? rs.getFloat("bs_unitario") : null)
                            .totalBsDesc(rs.getObject("total_bs_desc") != null ? rs.getFloat("total_bs_desc") : null)
                            .totalGeneralBs(rs.getObject("total_general_bs") != null ? rs.getFloat("total_general_bs") : null)
                            .tipoFila(tipoFila)
                            .build();

                    ventas.add(venta);
                }
            }
        } catch (SQLException e) {
            log.error("Error al obtener reporte de ventas", e);
            throw new RuntimeException("Error al obtener reporte de ventas: " + e.getMessage(), e);
        }

        if (ventas.isEmpty()) {
            log.warn("No se encontraron ventas para el rango de fechas: {} - {}", fechaDesde, fechaHasta);
        }

        return ventas;
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