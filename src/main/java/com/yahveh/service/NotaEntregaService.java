package com.yahveh.service;

import com.yahveh.dto.NotaEntregaReporteDTO;
import com.yahveh.dto.request.DetalleNotaEntregaRequest;
import com.yahveh.dto.request.NotaEntregaRequest;
import com.yahveh.dto.response.NotaEntregaResponse;
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
     * Listar solo notas válidas
     */
    public List<NotaEntregaResponse> listar() {
        log.info("Listando todas las notas de entrega válidas");
        List<NotaEntregaResponse> notas = notaEntregaRepository.listarTodas();

        // Cargar detalles para cada nota
        notas.forEach(nota -> {
            nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega()));
        });

        return notas;
    }

    /**
     * Listar todas las notas (válidas y anuladas)
     */
    public List<NotaEntregaResponse> listarTodasConAnuladas() {
        log.info("Listando todas las notas de entrega (válidas y anuladas)");
        List<NotaEntregaResponse> notas = notaEntregaRepository.listarTodasConAnuladas();

        notas.forEach(nota -> {
            nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega()));
        });

        return notas;
    }

    /**
     * Listar solo notas anuladas
     */
    public List<NotaEntregaResponse> listarAnuladas() {
        log.info("Listando notas de entrega anuladas");
        List<NotaEntregaResponse> notas = notaEntregaRepository.listarAnuladas();

        notas.forEach(nota -> {
            nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega()));
        });

        return notas;
    }

    public NotaEntregaResponse buscarPorCodigo(int codNotaEntrega) {
        log.info("Buscando nota de entrega: {}", codNotaEntrega);
        NotaEntregaResponse nota = notaEntregaRepository.buscarPorCodigo(codNotaEntrega)
                .orElseThrow(() -> new NotFoundException("Nota de entrega no encontrada"));

        // Cargar detalles
        nota.setDetalles(detalleRepository.listarPorNotaEntrega(codNotaEntrega));

        return nota;
    }

    public List<NotaEntregaResponse> listarPorCliente(int codCliente) {
        log.info("Listando notas de entrega del cliente: {}", codCliente);
        List<NotaEntregaResponse> notas = notaEntregaRepository.listarPorCliente(codCliente);

        notas.forEach(nota -> {
            nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega()));
        });

        return notas;
    }

    public List<NotaEntregaResponse> listarPorFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Listando notas de entrega entre {} y {}", fechaDesde, fechaHasta);
        List<NotaEntregaResponse> notas = notaEntregaRepository.listarPorFechas(fechaDesde, fechaHasta);

        notas.forEach(nota -> {
            nota.setDetalles(detalleRepository.listarPorNotaEntrega(nota.getCodNotaEntrega()));
        });

        return notas;
    }

    @Transactional
    public NotaEntregaResponse crear(NotaEntregaRequest request) {
        log.info("Creando nota de entrega para cliente: {}", request.getCodCliente());

        long audUsuario = securityUtils.getCurrentUserId();

        // Crear la nota de entrega (siempre con estado = 1 VÁLIDO)
        // ⭐ Ahora incluye codEmpleado
        long codNotaEntrega = notaEntregaRepository.crearNotaEntrega(
                request.getCodCliente(),
                request.getFecha(),
                request.getDireccion(),
                request.getZona(),
                audUsuario
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
        notaEntregaRepository.actualizarNotaEntrega(
                codNotaEntrega,
                request.getFecha(),
                request.getDireccion(),
                request.getZona(),
                audUsuario
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
        parametros.put("totalConFactura", reporte.getTotalConFactura());
        parametros.put("totalSinFactura", reporte.getTotalSinFactura());

        // Generar PDF con ambas copias
        return reporteService.generarReportePDF("nota_entrega", parametros, detallesDuplicados);
    }
}