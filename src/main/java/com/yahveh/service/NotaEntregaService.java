package com.yahveh.service;

import com.yahveh.dto.request.DetalleNotaEntregaRequest;
import com.yahveh.dto.request.NotaEntregaRequest;
import com.yahveh.dto.response.DetalleNotaEntregaResponse;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class NotaEntregaService {

    @Inject
    NotaEntregaRepository notaEntregaRepository;

    @Inject
    DetalleNotaEntregaRepository detalleRepository;

    @Inject
    SecurityUtils securityUtils;

    public List<NotaEntregaResponse> listar() {
        log.info("Listando todas las notas de entrega");
        List<NotaEntregaResponse> notas = notaEntregaRepository.listarTodas();

        // Cargar detalles en batch para evitar N+1 queries
        if (!notas.isEmpty()) {
            List<Integer> codNotasEntrega = notas.stream()
                    .map(NotaEntregaResponse::getCodNotaEntrega)
                    .collect(Collectors.toList());
            
            Map<Integer, List<DetalleNotaEntregaResponse>> detallesPorNota = 
                    detalleRepository.listarPorNotasEntregaBatch(codNotasEntrega);
            
            notas.forEach(nota -> {
                nota.setDetalles(detallesPorNota.getOrDefault(nota.getCodNotaEntrega(), List.of()));
            });
        }

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

        // Cargar detalles en batch para evitar N+1 queries
        if (!notas.isEmpty()) {
            List<Integer> codNotasEntrega = notas.stream()
                    .map(NotaEntregaResponse::getCodNotaEntrega)
                    .collect(Collectors.toList());
            
            Map<Integer, List<DetalleNotaEntregaResponse>> detallesPorNota = 
                    detalleRepository.listarPorNotasEntregaBatch(codNotasEntrega);
            
            notas.forEach(nota -> {
                nota.setDetalles(detallesPorNota.getOrDefault(nota.getCodNotaEntrega(), List.of()));
            });
        }

        return notas;
    }

    public List<NotaEntregaResponse> listarPorFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Listando notas de entrega entre {} y {}", fechaDesde, fechaHasta);
        List<NotaEntregaResponse> notas = notaEntregaRepository.listarPorFechas(fechaDesde, fechaHasta);

        // Cargar detalles en batch para evitar N+1 queries
        if (!notas.isEmpty()) {
            List<Integer> codNotasEntrega = notas.stream()
                    .map(NotaEntregaResponse::getCodNotaEntrega)
                    .collect(Collectors.toList());
            
            Map<Integer, List<DetalleNotaEntregaResponse>> detallesPorNota = 
                    detalleRepository.listarPorNotasEntregaBatch(codNotasEntrega);
            
            notas.forEach(nota -> {
                nota.setDetalles(detallesPorNota.getOrDefault(nota.getCodNotaEntrega(), List.of()));
            });
        }

        return notas;
    }

    @Transactional
    public NotaEntregaResponse crear(NotaEntregaRequest request) {
        log.info("Creando nota de entrega para cliente: {}", request.getCodCliente());

        int audUsuario = securityUtils.getCurrentUserId();

        // Crear la nota de entrega
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
                        audUsuario
                );
            }
        }

        return buscarPorCodigo((int) codNotaEntrega);
    }

    @Transactional
    public NotaEntregaResponse actualizar(int codNotaEntrega, NotaEntregaRequest request) {
        log.info("Actualizando nota de entrega: {}", codNotaEntrega);

        int audUsuario = securityUtils.getCurrentUserId();

        notaEntregaRepository.actualizarNotaEntrega(
                codNotaEntrega,
                request.getCodCliente(),
                request.getFecha(),
                request.getDireccion(),
                request.getZona(),
                audUsuario
        );

        return buscarPorCodigo(codNotaEntrega);
    }

    @Transactional
    public void eliminar(int codNotaEntrega) {
        log.info("Eliminando nota de entrega: {}", codNotaEntrega);

        int audUsuario = securityUtils.getCurrentUserId();

        notaEntregaRepository.eliminarNotaEntrega(codNotaEntrega, audUsuario);
    }
}