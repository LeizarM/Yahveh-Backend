package com.yahveh.service;

import com.yahveh.dto.request.DetalleNotaEntregaRequest;
import com.yahveh.dto.response.DetalleNotaEntregaResponse;
import com.yahveh.repository.DetalleNotaEntregaRepository;
import com.yahveh.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class DetalleNotaEntregaService {

    @Inject
    DetalleNotaEntregaRepository detalleRepository;

    @Inject
    SecurityUtils securityUtils;

    public List<DetalleNotaEntregaResponse> listarPorNotaEntrega(int codNotaEntrega) {
        log.info("Listando detalles de la nota de entrega: {}", codNotaEntrega);
        return detalleRepository.listarPorNotaEntrega(codNotaEntrega);
    }

    public DetalleNotaEntregaResponse buscarPorCodigo(int codDetalle) {
        log.info("Buscando detalle: {}", codDetalle);
        return detalleRepository.buscarPorCodigo(codDetalle)
                .orElseThrow(() -> new NotFoundException("Detalle no encontrado"));
    }

    public DetalleNotaEntregaResponse crear(int codNotaEntrega, DetalleNotaEntregaRequest request) {
        log.info("Creando detalle para nota de entrega: {}", codNotaEntrega);

        int audUsuario = securityUtils.getCurrentUserId();

        long codDetalle = detalleRepository.crearDetalle(
                codNotaEntrega,
                request.getCodArticulo(),
                request.getCantidad(),
                request.getPrecioUnitario(),
                request.getPrecioSinFactura(),
                audUsuario
        );

        return buscarPorCodigo((int) codDetalle);
    }

    public DetalleNotaEntregaResponse actualizar(int codDetalle, DetalleNotaEntregaRequest request) {
        log.info("Actualizando detalle: {}", codDetalle);

        int audUsuario = securityUtils.getCurrentUserId();

        detalleRepository.actualizarDetalle(
                codDetalle,
                request.getCantidad(),
                request.getPrecioUnitario(),
                request.getPrecioSinFactura(),
                audUsuario
        );

        return buscarPorCodigo(codDetalle);
    }

    public void eliminar(int codDetalle) {
        log.info("Eliminando detalle: {}", codDetalle);

        int audUsuario = securityUtils.getCurrentUserId();

        detalleRepository.eliminarDetalle(codDetalle, audUsuario);
    }
}