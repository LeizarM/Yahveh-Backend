package com.yahveh.service;

import com.yahveh.dto.ArticuloVendedorReporteDTO;
import com.yahveh.dto.InventarioReporteDTO;
import com.yahveh.dto.MovimientoInventarioReporteDTO;
import com.yahveh.dto.NotaEntregaReporteDTO;
import com.yahveh.dto.VendedorReporteDTO;
import com.yahveh.dto.VentaReporteDTO;
import com.yahveh.dto.request.DetalleNotaEntregaRequest;
import com.yahveh.dto.request.NotaEntregaRequest;
import com.yahveh.dto.response.NotaEntregaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.repository.DetalleNotaEntregaRepository;
import com.yahveh.repository.NotaEntregaRepository;
import com.yahveh.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;

@ApplicationScoped
@Slf4j
public class NotaEntregaService {

    @Inject
    NotaEntregaRepository notaEntregaRepository;

    @Inject
    DetalleNotaEntregaRepository detalleRepository;

    @Inject
    SecurityUtils securityUtils;

    @Inject
    ReporteService reporteService;

    /**
     * Helper: obtiene el codEmpleado del usuario actual
     */
    private Integer obtenerCodEmpleadoActual() {
        Integer codEmpleado = notaEntregaRepository.obtenerCodEmpleadoDeUsuario(securityUtils.getCurrentUserId());
        if (codEmpleado == null) {
            log.warn("El usuario {} no tiene codEmpleado asociado", securityUtils.getCurrentUsername());
        }
        return codEmpleado;
    }

    /**
     * Listar notas válidas — admin ve todas, vendedor solo las suyas (filtradas por codEmpleado)
     */
    public List<NotaEntregaResponse> listar() {
        log.info("Listando notas de entrega válidas");
        List<NotaEntregaResponse> notas;

        if (securityUtils.isAdmin()) {
            notas = notaEntregaRepository.listarTodas();
        } else {
            Integer codEmp = obtenerCodEmpleadoActual();
            notas = codEmp != null
                    ? notaEntregaRepository.listarPorEmpleado(codEmp)
                    : new ArrayList<>();
        }

        notas.forEach(nota ->
                nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega())));
        return notas;
    }

    /**
     * Listar todas las notas (válidas + anuladas) — admin ve todas, vendedor solo las suyas
     */
    public List<NotaEntregaResponse> listarTodasConAnuladas() {
        log.info("Listando todas las notas de entrega (válidas y anuladas)");
        List<NotaEntregaResponse> notas;

        if (securityUtils.isAdmin()) {
            notas = notaEntregaRepository.listarTodasConAnuladas();
        } else {
            Integer codEmp = obtenerCodEmpleadoActual();
            notas = codEmp != null
                    ? notaEntregaRepository.listarTodasConAnuladasPorEmpleado(codEmp)
                    : new ArrayList<>();
        }

        notas.forEach(nota ->
                nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega())));
        return notas;
    }

    /**
     * Listar solo notas anuladas — admin ve todas, vendedor solo las suyas
     */
    public List<NotaEntregaResponse> listarAnuladas() {
        log.info("Listando notas de entrega anuladas");
        List<NotaEntregaResponse> notas;

        if (securityUtils.isAdmin()) {
            notas = notaEntregaRepository.listarAnuladas();
        } else {
            Integer codEmp = obtenerCodEmpleadoActual();
            notas = codEmp != null
                    ? notaEntregaRepository.listarAnuladasPorEmpleado(codEmp)
                    : new ArrayList<>();
        }

        notas.forEach(nota ->
                nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega())));
        return notas;
    }

    public NotaEntregaResponse buscarPorCodigo(int codNotaEntrega) {
        log.info("Buscando nota de entrega: {}", codNotaEntrega);
        NotaEntregaResponse nota = notaEntregaRepository.buscarPorCodigo(codNotaEntrega)
                .orElseThrow(() -> new NotFoundException("Nota de entrega no encontrada"));

        nota.setDetalles(detalleRepository.listarPorNotaEntrega(codNotaEntrega));
        return nota;
    }

    /**
     * Listar por cliente — admin ve todas las del cliente, vendedor solo las suyas de ese cliente
     */
    public List<NotaEntregaResponse> listarPorCliente(int codCliente) {
        log.info("Listando notas de entrega del cliente: {}", codCliente);
        List<NotaEntregaResponse> notas;

        if (securityUtils.isAdmin()) {
            notas = notaEntregaRepository.listarPorCliente(codCliente);
        } else {
            Integer codEmp = obtenerCodEmpleadoActual();
            notas = codEmp != null
                    ? notaEntregaRepository.listarPorClientePorEmpleado(codCliente, codEmp)
                    : new ArrayList<>();
        }

        notas.forEach(nota ->
                nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega())));
        return notas;
    }

    /**
     * Listar por fechas — admin ve todas, vendedor solo las suyas
     */
    public List<NotaEntregaResponse> listarPorFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Listando notas de entrega entre {} y {}", fechaDesde, fechaHasta);
        List<NotaEntregaResponse> notas;

        if (securityUtils.isAdmin()) {
            notas = notaEntregaRepository.listarPorFechas(fechaDesde, fechaHasta);
        } else {
            Integer codEmp = obtenerCodEmpleadoActual();
            notas = codEmp != null
                    ? notaEntregaRepository.listarPorFechasPorEmpleado(fechaDesde, fechaHasta, codEmp)
                    : new ArrayList<>();
        }

        notas.forEach(nota ->
                nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega())));
        return notas;
    }

    @Transactional
    public NotaEntregaResponse crear(NotaEntregaRequest request) {
        log.info("Creando nota de entrega para cliente: {}", request.getCodCliente());

        long audUsuario = securityUtils.getCurrentUserId();

        // ⭐ Determinar el codEmpleado: usar el del request si viene, sino el del usuario actual
        Integer codEmpleado = request.getCodEmpleado() != null
                ? request.getCodEmpleado()
                : obtenerCodEmpleadoActual();

        // Crear la nota de entrega (siempre con estado = 1 VÁLIDO)
        // ⭐ Ahora incluye nit y codEmpleado
        long codNotaEntrega = notaEntregaRepository.crearNotaEntrega(
                request.getCodCliente(),
                request.getFecha(),
                request.getDireccion(),
                request.getZona(),
                audUsuario,
                request.getNit(),
                codEmpleado
        );

        // Agregar detalles si existen
        if (request.getDetalles() != null && !request.getDetalles().isEmpty()) {
            for (DetalleNotaEntregaRequest detalle : request.getDetalles()) {
                detalleRepository.crearDetalle(
                        (int) codNotaEntrega,
                        detalle.getCodArticulo(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getPrecioSinFactura(),
                        detalle.getDescuento(),
                        (int) audUsuario
                );
            }
        }

        return buscarPorCodigo((int) codNotaEntrega);
    }

    @Transactional
    public NotaEntregaResponse actualizar(int codNotaEntrega, NotaEntregaRequest request) {
        log.info("Actualizando nota de entrega: {}", codNotaEntrega);

        long audUsuario = securityUtils.getCurrentUserId();

        // ⭐ Ya no se pasa codCliente (no se puede cambiar el cliente)
        // ⭐ Ahora también pasa nit y codEmpleado (opcionales)
        notaEntregaRepository.actualizarNotaEntrega(
                codNotaEntrega,
                request.getFecha(),
                request.getDireccion(),
                request.getZona(),
                audUsuario,
                request.getNit(),
                request.getCodEmpleado()
        );

        return buscarPorCodigo(codNotaEntrega);
    }

    /**
     * ⭐ ANULAR nota de entrega (devuelve stock automáticamente)
     */
    @Transactional
    public NotaEntregaResponse anular(int codNotaEntrega) {
        log.info("Anulando nota de entrega: {}", codNotaEntrega);

        int audUsuario = securityUtils.getCurrentUserId();

        notaEntregaRepository.anularNotaEntrega(codNotaEntrega, audUsuario);

        return buscarPorCodigo(codNotaEntrega);
    }

    @Transactional
    public void eliminar(int codNotaEntrega) {
        log.info("Eliminando nota de entrega: {}", codNotaEntrega);

        int audUsuario = securityUtils.getCurrentUserId();

        notaEntregaRepository.eliminarNotaEntrega(codNotaEntrega, audUsuario);
    }

    /**
     * ⭐ Obtener datos del reporte de ventas (sin generar PDF)
     */
    public List<VentaReporteDTO> obtenerDatosReporteVentas(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Obteniendo datos del reporte de ventas desde {} hasta {}", fechaDesde, fechaHasta);

        List<VentaReporteDTO> ventas = notaEntregaRepository.obtenerReporteVentas(fechaDesde, fechaHasta);

        if (ventas.isEmpty()) {
            log.warn("No hay datos para el reporte de ventas en el período especificado");
        }

        log.info("Se obtuvieron {} registros del reporte", ventas.size());

        return ventas;
    }

    public byte[] generarPDF(Long codNotaEntrega) {
        log.info("Generando PDF para nota de entrega: {}", codNotaEntrega);

        // Obtener datos del reporte
        NotaEntregaReporteDTO reporte = notaEntregaRepository.obtenerDatosReporte(codNotaEntrega);

        // Crear lista duplicada con campo tipoCopia
        List<Map<String, Object>> detallesDuplicados = new ArrayList<>();

        // Primera copia: CLIENTE
        for (NotaEntregaReporteDTO.DetalleArticuloDTO detalle : reporte.getDetalles()) {
            Map<String, Object> item = new HashMap<>();
            item.put("codArticulo", detalle.getCodArticulo());
            item.put("lineaArticulo", detalle.getLineaArticulo());
            item.put("descripcionArticulo", detalle.getDescripcionArticulo());
            item.put("cantidad", detalle.getCantidad());
            item.put("precioUnitario", detalle.getPrecioUnitario());
            item.put("descuento", detalle.getDescuento());
            item.put("precioConDescuento", detalle.getPrecioConDescuento());    // ⭐ Nuevo
            item.put("precioTotal", detalle.getPrecioTotal());
            item.put("precioSinFactura", detalle.getPrecioSinFactura());
            item.put("subtotalSinFactura", detalle.getSubtotalSinFactura());
            item.put("tipoCopia", "COPIA CLIENTE");
            detallesDuplicados.add(item);
        }

        // Segunda copia: EMPLEADO
        for (NotaEntregaReporteDTO.DetalleArticuloDTO detalle : reporte.getDetalles()) {
            Map<String, Object> item = new HashMap<>();
            item.put("codArticulo", detalle.getCodArticulo());
            item.put("lineaArticulo", detalle.getLineaArticulo());
            item.put("descripcionArticulo", detalle.getDescripcionArticulo());
            item.put("cantidad", detalle.getCantidad());
            item.put("precioUnitario", detalle.getPrecioUnitario());
            item.put("descuento", detalle.getDescuento());
            item.put("precioConDescuento", detalle.getPrecioConDescuento());    // ⭐ Nuevo
            item.put("precioTotal", detalle.getPrecioTotal());
            item.put("precioSinFactura", detalle.getPrecioSinFactura());
            item.put("subtotalSinFactura", detalle.getSubtotalSinFactura());
            item.put("tipoCopia", "COPIA EMPLEADO");
            detallesDuplicados.add(item);
        }

        // Preparar parámetros
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("codNotaEntrega", reporte.getCodNotaEntrega());
        parametros.put("fecha", java.sql.Date.valueOf(reporte.getFecha()));
        parametros.put("codCliente", reporte.getCodCliente());
        parametros.put("nombreCliente", reporte.getNombreCliente());
        parametros.put("nit", reporte.getNit());
        parametros.put("razonSocial", reporte.getRazonSocial());
        parametros.put("direccion", reporte.getDireccion());
        parametros.put("zona", reporte.getZona());
        parametros.put("telefonos", reporte.getTelefonos());
        parametros.put("estado", reporte.getEstado());                  // ⭐ Nuevo
        parametros.put("estadoTexto", reporte.getEstadoTexto());        // ⭐ Nuevo
        parametros.put("nombreEmpleado", reporte.getNombreEmpleado() != null
                ? reporte.getNombreEmpleado() : "Sin asignar");          // ⭐ Nuevo
        parametros.put("totalConFactura", reporte.getTotalConFactura());
        parametros.put("totalSinFactura", reporte.getTotalSinFactura());

        // Generar PDF con ambas copias
        return reporteService.generarReportePDF("nota_entrega", parametros, detallesDuplicados);
    }

    public byte[] generarReporteVendedores(LocalDate fechaDesde, LocalDate fechaHasta) {
        return generarReporteVendedores(fechaDesde, fechaHasta, null);
    }

    /**
     * Generar reporte de vendedores, opcionalmente filtrado por un empleado.
     * Si codEmpleado es null, devuelve todos los vendedores.
     *
     * El PDF resultante combina 3 secciones:
     *   1. Resumen general por vendedor (totales de notas y Bs.)
     *   2. Detalle de notas por vendedor
     *   3. ⭐ Detalle de ARTÍCULOS vendidos por cada nota
     */
    public byte[] generarReporteVendedores(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            Integer codEmpleado) {
        log.info("Generando reporte de vendedores desde {} hasta {} — empleado: {}",
                fechaDesde, fechaHasta, codEmpleado);

        List<VendedorReporteDTO> datos = notaEntregaRepository.obtenerReporteVendedores(
                fechaDesde, fechaHasta, codEmpleado);

        if (datos.isEmpty()) {
            throw new BusinessException("No hay datos disponibles para el período seleccionado");
        }

        // ⭐ Detalle de artículos vendidos
        List<ArticuloVendedorReporteDTO> articulos = notaEntregaRepository
                .obtenerArticulosPorVendedor(fechaDesde, fechaHasta, codEmpleado);

        Map<String, Object> params = new HashMap<>();
        params.put("fechaDesde", java.sql.Date.valueOf(fechaDesde));
        params.put("fechaHasta", java.sql.Date.valueOf(fechaHasta));
        // Si se filtra por empleado, mostramos el nombre en el título
        params.put("vendedorFiltro",
                codEmpleado != null && !datos.isEmpty()
                        ? datos.get(0).getNombreVendedor()
                        : "TODOS LOS VENDEDORES");

        byte[] resumen = reporteService.generarReportePDF("reporte_vendedores_resumen", params, datos);
        byte[] detalle = reporteService.generarReportePDF("reporte_vendedores_detalle", params, datos);

        // ⭐ Tercer PDF: detalle de artículos vendidos por vendedor
        byte[] articulosPdf = articulos.isEmpty()
                ? new byte[0]
                : reporteService.generarReportePDF("reporte_vendedores_articulos", params, articulos);

        // Concatenar todos los PDFs (mergePDFs admite múltiples)
        if (articulosPdf.length > 0) {
            byte[] partial = reporteService.mergePDFs(resumen, detalle);
            return reporteService.mergePDFs(partial, articulosPdf);
        }
        return reporteService.mergePDFs(resumen, detalle);
    }

    /**
     * Reporte de MOVIMIENTOS de inventario entre fechas.
     * Lista cada entrada, salida o ajuste con su saldo, valor y observación.
     */
    public byte[] generarReporteMovimientosInventario(
            LocalDate fechaDesde,
            LocalDate fechaHasta,
            String codArticulo) {
        log.info("Generando reporte de movimientos: {} - {} (artículo: {})",
                fechaDesde, fechaHasta, codArticulo);

        List<MovimientoInventarioReporteDTO> datos =
                notaEntregaRepository.obtenerReporteMovimientos(fechaDesde, fechaHasta, codArticulo);

        if (datos.isEmpty()) {
            throw new BusinessException(
                    "No hay movimientos de inventario en el período seleccionado");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fechaDesde", java.sql.Date.valueOf(fechaDesde));
        params.put("fechaHasta", java.sql.Date.valueOf(fechaHasta));
        params.put("codArticulo",
                codArticulo == null || codArticulo.isBlank() ? "TODOS" : codArticulo);

        return reporteService.generarReportePDF(
                "reporte_movimientos_inventario", params, datos);
    }

    public byte[] generarReporteInventario(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Generando reporte de inventario desde {} hasta {}", fechaDesde, fechaHasta);

        List<InventarioReporteDTO> datos = notaEntregaRepository.obtenerReporteInventario(fechaDesde, fechaHasta);

        if (datos.isEmpty()) {
            throw new BusinessException("No hay artículos disponibles para el período seleccionado");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("fechaDesde", java.sql.Date.valueOf(fechaDesde));
        params.put("fechaHasta", java.sql.Date.valueOf(fechaHasta));

        return reporteService.generarReportePDF("reporte_inventario", params, datos);
    }

    /**
     * ⭐ Generar reporte de ventas mensual
     */
    public byte[] generarReporteVentas(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Generando reporte de ventas desde {} hasta {}", fechaDesde, fechaHasta);

        try {
            // 1. Obtener datos
            List<VentaReporteDTO> ventas = notaEntregaRepository.obtenerReporteVentas(fechaDesde, fechaHasta);

            if (ventas.isEmpty()) {
                log.warn("No hay datos para el reporte de ventas");
                throw new BusinessException("No hay datos disponibles para el período seleccionado");
            }

            // 2. Preparar parámetros (convertir LocalDate a java.util.Date)
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaDesde", java.sql.Date.valueOf(fechaDesde));
            parametros.put("fechaHasta", java.sql.Date.valueOf(fechaHasta));
            parametros.put("tituloReporte", "REPORTE DE VENTAS MENSUAL");

            // 3. Generar PDF usando el ReporteService
            byte[] pdfBytes = reporteService.generarReportePDF("reporte_ventas", parametros, ventas);

            log.info("Reporte de ventas generado. Tamaño: {} bytes, Registros: {}", pdfBytes.length, ventas.size());

            return pdfBytes;

        } catch (Exception e) {
            log.error("Error al generar reporte de ventas", e);
            throw new RuntimeException("Error al generar reporte de ventas: " + e.getMessage(), e);
        }
    }
}