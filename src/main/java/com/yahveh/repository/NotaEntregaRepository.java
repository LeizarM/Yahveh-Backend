package com.yahveh.repository;

import com.yahveh.dto.ArticuloVendedorReporteDTO;
import com.yahveh.dto.InventarioReporteDTO;
import com.yahveh.dto.MovimientoInventarioReporteDTO;
import com.yahveh.dto.NotaEntregaReporteDTO;
import com.yahveh.dto.VendedorReporteDTO;
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
     * Obtener el codEmpleado de un usuario dado su codUsuario
     */
    public Integer obtenerCodEmpleadoDeUsuario(int codUsuario) {
        String sql = "SELECT \"codEmpleado\" FROM tb_usuario WHERE \"codUsuario\" = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, codUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int codEmp = rs.getInt(1);
                    return rs.wasNull() ? null : codEmp;
                }
            }
        } catch (SQLException e) {
            log.error("Error al obtener codEmpleado del usuario {}", codUsuario, e);
        }
        return null;
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

    // ── Listados filtrados por empleado (usuario no-admin) ──────────────────
    // ⭐ Ahora filtran por codEmpleado en lugar de aud_usuario para mejor trazabilidad

    public List<NotaEntregaResponse> listarPorEmpleado(int codEmpleado) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_estado := 1, p_codempleado := ?)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, codEmpleado);
    }

    public List<NotaEntregaResponse> listarTodasConAnuladasPorEmpleado(int codEmpleado) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_codempleado := ?)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, codEmpleado);
    }

    public List<NotaEntregaResponse> listarAnuladasPorEmpleado(int codEmpleado) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_estado := 0, p_codempleado := ?)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, codEmpleado);
    }

    public List<NotaEntregaResponse> listarPorFechasPorEmpleado(LocalDate fechaDesde, LocalDate fechaHasta, int codEmpleado) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_fecha_desde := ?, p_fecha_hasta := ?, p_estado := 1, p_codempleado := ?)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, fechaDesde, fechaHasta, codEmpleado);
    }

    public List<NotaEntregaResponse> listarPorClientePorEmpleado(long codCliente, int codEmpleado) {
        String sql = "SELECT * FROM p_list_nota_entrega(p_codcliente := ?, p_estado := 1, p_codempleado := ?)";
        return executeQueryList(sql, this::mapNotaEntregaResponse, codCliente, codEmpleado);
    }

    // ────────────────────────────────────────────────────────────────────────

    /**
     * Crear nueva nota de entrega
     * ⭐ Ahora incluye nit y codEmpleado (snapshot histórico)
     */
    public int crearNotaEntrega(long codCliente, LocalDate fecha,
                                String direccion, String zona, long audUsuario,
                                String nit, Integer codEmpleado) {
        String sql = """
        SELECT p_error, p_errormsg, p_result
        FROM p_abm_nota_entrega(
            p_codcliente := ?::BIGINT,
            p_fecha := ?::DATE,
            p_direccion := ?::VARCHAR,
            p_zona := ?::VARCHAR,
            p_audusuario := ?::BIGINT,
            p_accion := 'I'::VARCHAR,
            p_nit := ?::VARCHAR,
            p_codempleado := ?::BIGINT
        )
        """;

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codCliente, fecha, direccion, zona, audUsuario, nit, codEmpleado)
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
     * ⭐ Ahora incluye nit y codEmpleado opcionales
     */
    public void actualizarNotaEntrega(long codNotaEntrega, LocalDate fecha,
                                      String direccion, String zona, long audUsuario,
                                      String nit, Integer codEmpleado) {
        String sql = """
        SELECT p_error, p_errormsg, p_result
        FROM p_abm_nota_entrega(
            p_codnotaentrega := ?::BIGINT,
            p_fecha := ?::DATE,
            p_direccion := ?::VARCHAR,
            p_zona := ?::VARCHAR,
            p_audusuario := ?::BIGINT,
            p_accion := 'U'::VARCHAR,
            p_nit := ?::VARCHAR,
            p_codempleado := ?::BIGINT
        )
        """;

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codNotaEntrega, fecha, direccion, zona, audUsuario, nit, codEmpleado)
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

                    // ⭐ Empleado que creó la nota
                    long codEmpVal = rs.getLong("cod_empleado");
                    row.put("codEmpleado", rs.wasNull() ? null : codEmpVal);
                    row.put("nombreEmpleado", rs.getString("nombre_empleado"));

                    // Detalle
                    row.put("codArticulo", rs.getString("cod_articulo"));
                    row.put("lineaArticulo", rs.getString("linea_articulo"));
                    row.put("descripcionArticulo", rs.getString("descripcion_articulo"));
                    row.put("cantidad", rs.getInt("cantidad"));
                    row.put("precioUnitario", rs.getFloat("precio_unitario"));
                    row.put("descuento", rs.getFloat("descuento"));
                    row.put("precioConDescuento", rs.getFloat("precio_con_descuento"));  // ⭐ Nuevo
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
                        .descuento((Float) row.get("descuento"))
                        .precioConDescuento((Float) row.get("precioConDescuento"))   // ⭐ Nuevo
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
                .codEmpleado((Long) primerRegistro.get("codEmpleado"))         // ⭐ Nuevo
                .nombreEmpleado((String) primerRegistro.get("nombreEmpleado")) // ⭐ Nuevo
                .totalConFactura((Float) primerRegistro.get("totalGeneral"))
                .totalSinFactura((Float) primerRegistro.get("totalSinFactura"))
                .totalArticulos((Integer) primerRegistro.get("totalArticulos"))
                .detalles(detalles)
                .build();
    }

    /**
     * Mapear ResultSet a NotaEntregaResponse
     * ⭐ Ahora incluye nit y codEmpleado
     */
    private NotaEntregaResponse mapNotaEntregaResponse(ResultSet rs) throws SQLException {
        int codEmp = rs.getInt("cod_empleado");
        Integer codEmpleado = rs.wasNull() ? null : codEmp;

        return NotaEntregaResponse.builder()
                .codNotaEntrega(rs.getInt("cod_nota_entrega"))
                .codCliente(rs.getInt("cod_cliente"))
                .nombreCliente(rs.getString("nombre_cliente"))
                .fecha(rs.getDate("fecha").toLocalDate())
                .direccion(rs.getString("direccion"))
                .zona(rs.getString("zona"))
                .nit(rs.getString("nit"))
                .codEmpleado(codEmpleado)
                .audUsuario(rs.getInt("aud_usuario"))
                .nombreEmpleado(rs.getString("nombre_empleado"))
                .audFecha(rs.getTimestamp("aud_fecha").toLocalDateTime())
                .estado(rs.getInt("estado"))
                .estadoTexto(rs.getString("estado_texto"))
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


    public List<VendedorReporteDTO> obtenerReporteVendedores(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer codEmpleado) {
        String sql = "SELECT * FROM p_reporte_notas_vendedor(?, ?, ?)";

        List<VendedorReporteDTO> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fechaDesde));
            stmt.setDate(2, java.sql.Date.valueOf(fechaHasta));
            if (codEmpleado != null) {
                stmt.setInt(3, codEmpleado);
            } else {
                stmt.setNull(3, java.sql.Types.BIGINT);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long codEmp = rs.getLong("cod_empleado");
                    Long codEmpVal = rs.wasNull() ? null : codEmp;

                    result.add(VendedorReporteDTO.builder()
                            .codEmpleado(codEmpVal)
                            .nombreVendedor(rs.getString("nombre_vendedor"))
                            .fecha(rs.getDate("fecha") != null ? rs.getDate("fecha").toLocalDate() : null)
                            .codNotaEntrega(rs.getLong("cod_nota_entrega"))
                            .nombreCliente(rs.getString("nombre_cliente"))
                            .nit(rs.getString("nit"))
                            .direccion(rs.getString("direccion"))
                            .zona(rs.getString("zona"))
                            .cantidadArticulos(rs.getInt("cantidad_articulos"))
                            .totalBs(rs.getFloat("total_bs"))
                            .build());
                }
            }
        } catch (SQLException e) {
            log.error("Error al obtener reporte de vendedores", e);
            throw new RuntimeException("Error al obtener reporte de vendedores: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Detalle de artículos vendidos por vendedor (opcionalmente filtrado por empleado).
     * Se usa para el reporte de detalle de ventas por vendedor.
     */
    public List<ArticuloVendedorReporteDTO> obtenerArticulosPorVendedor(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer codEmpleado) {
        String sql = "SELECT * FROM p_reporte_articulos_vendedor(?, ?, ?)";

        List<ArticuloVendedorReporteDTO> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fechaDesde));
            stmt.setDate(2, java.sql.Date.valueOf(fechaHasta));
            if (codEmpleado != null) {
                stmt.setInt(3, codEmpleado);
            } else {
                stmt.setNull(3, java.sql.Types.BIGINT);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long codEmp = rs.getLong("cod_empleado");
                    Long codEmpVal = rs.wasNull() ? null : codEmp;

                    result.add(ArticuloVendedorReporteDTO.builder()
                            .codEmpleado(codEmpVal)
                            .nombreVendedor(rs.getString("nombre_vendedor"))
                            .codNotaEntrega(rs.getLong("cod_nota_entrega"))
                            .fecha(rs.getDate("fecha") != null
                                    ? rs.getDate("fecha").toLocalDate() : null)
                            .nombreCliente(rs.getString("nombre_cliente"))
                            .nit(rs.getString("nit"))
                            .direccion(rs.getString("direccion"))
                            .zona(rs.getString("zona"))
                            .codDetalle(rs.getLong("cod_detalle"))
                            .codArticulo(rs.getString("cod_articulo"))
                            .descripcionArticulo(rs.getString("descripcion_articulo"))
                            .lineaArticulo(rs.getString("linea_articulo"))
                            .cantidad(rs.getInt("cantidad"))
                            .precioUnitario(rs.getBigDecimal("precio_unitario"))
                            .descuento(rs.getBigDecimal("descuento"))
                            .precioConDescuento(rs.getBigDecimal("precio_con_descuento"))
                            .subtotal(rs.getBigDecimal("subtotal"))
                            .build());
                }
            }
        } catch (SQLException e) {
            log.error("Error al obtener artículos por vendedor", e);
            throw new RuntimeException("Error al obtener artículos por vendedor: " + e.getMessage(), e);
        }

        return result;
    }

    /**
     * Obtiene el detalle de movimientos de inventario entre fechas.
     * Opcionalmente filtrado por código de artículo.
     */
    public List<MovimientoInventarioReporteDTO> obtenerReporteMovimientos(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String codArticulo) {
        String sql = "SELECT * FROM p_reporte_movimientos_inventario(?, ?, ?)";

        List<MovimientoInventarioReporteDTO> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fechaDesde));
            stmt.setDate(2, java.sql.Date.valueOf(fechaHasta));
            if (codArticulo == null || codArticulo.isBlank()) {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(3, codArticulo.trim());
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(MovimientoInventarioReporteDTO.builder()
                            .codInventario(rs.getLong("cod_inventario"))
                            .fecha(rs.getDate("fecha") != null
                                    ? rs.getDate("fecha").toLocalDate() : null)
                            .codArticulo(rs.getString("cod_articulo"))
                            .descripcionArticulo(rs.getString("descripcion_articulo"))
                            .lineaArticulo(rs.getString("linea_articulo"))
                            .tipoMovimiento(rs.getString("tipo_movimiento"))
                            .cantidad(rs.getInt("cantidad"))
                            .saldoAnterior(rs.getInt("saldo_anterior"))
                            .saldoNuevo(rs.getInt("saldo_nuevo"))
                            .precioUnitario(rs.getBigDecimal("precio_unitario"))
                            .valorTotal(rs.getBigDecimal("valor_total"))
                            .observacion(rs.getString("observacion"))
                            .nombreUsuario(rs.getString("nombre_usuario"))
                            .build());
                }
            }
        } catch (SQLException e) {
            log.error("Error al obtener reporte de movimientos de inventario", e);
            throw new RuntimeException("Error al obtener reporte de movimientos: " + e.getMessage(), e);
        }

        return result;
    }

    public List<InventarioReporteDTO> obtenerReporteInventario(LocalDate fechaDesde, LocalDate fechaHasta) {
        String sql = "SELECT * FROM p_reporte_inventario(?, ?)";

        List<InventarioReporteDTO> result = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(fechaDesde));
            stmt.setDate(2, java.sql.Date.valueOf(fechaHasta));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(InventarioReporteDTO.builder()
                            .codArticulo(rs.getString("cod_articulo"))
                            .descripcion(rs.getString("descripcion"))
                            .codLinea(rs.getString("cod_linea"))
                            .descripcionLinea(rs.getString("descripcion_linea"))
                            .codFamilia(rs.getString("cod_familia"))
                            .descripcionFamilia(rs.getString("descripcion_familia"))
                            .entradas(rs.getInt("entradas"))
                            .salidas(rs.getInt("salidas"))
                            .stockActual(rs.getInt("stock_actual"))
                            .build());
                }
            }
        } catch (SQLException e) {
            log.error("Error al obtener reporte de inventario", e);
            throw new RuntimeException("Error al obtener reporte de inventario: " + e.getMessage(), e);
        }

        return result;
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