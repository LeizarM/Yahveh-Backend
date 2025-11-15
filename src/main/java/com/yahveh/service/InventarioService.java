package com.yahveh.service;


import com.yahveh.dto.request.InventarioRequest;
import com.yahveh.dto.response.InventarioResponse;
import com.yahveh.repository.InventarioRepository;
import com.yahveh.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@Slf4j
public class InventarioService {

    @Inject
    InventarioRepository inventarioRepository;

    @Inject
    SecurityUtils securityUtils;

    public List<InventarioResponse> listar() {
        log.info("Listando todos los movimientos de inventario");
        return inventarioRepository.listarTodos();
    }

    public InventarioResponse buscarPorCodigo(int codInventario) {
        log.info("Buscando movimiento de inventario: {}", codInventario);
        return inventarioRepository.buscarPorCodigo(codInventario)
                .orElseThrow(() -> new NotFoundException("Movimiento de inventario no encontrado"));
    }

    public List<InventarioResponse> listarPorArticulo(String codArticulo) {
        log.info("Listando movimientos del artículo: {}", codArticulo);
        return inventarioRepository.listarPorArticulo(codArticulo);
    }

    public List<InventarioResponse> listarPorTipo(String tipoMovimiento) {
        log.info("Listando movimientos por tipo: {}", tipoMovimiento);
        return inventarioRepository.listarPorTipo(tipoMovimiento);
    }

    public List<InventarioResponse> listarPorFechas(LocalDate fechaDesde, LocalDate fechaHasta) {
        log.info("Listando movimientos entre {} y {}", fechaDesde, fechaHasta);
        return inventarioRepository.listarPorFechas(fechaDesde, fechaHasta);
    }

    public InventarioResponse crear(InventarioRequest request) {
        log.info("Creando movimiento de inventario para artículo: {}", request.getCodArticulo());

        int audUsuario = securityUtils.getCurrentUserId();

        long codInventario = inventarioRepository.crearMovimiento(
                request.getCodArticulo(),
                request.getTipoMovimiento(),
                request.getCantidad(),
                request.getPrecioUnitario(),
                request.getFecha(),
                request.getObservacion(),
                audUsuario
        );

        return buscarPorCodigo((int) codInventario);
    }

    public InventarioResponse modificar(int codInventario, InventarioRequest request) {
        log.info("Modificando observación del movimiento: {}", codInventario);

        int audUsuario = securityUtils.getCurrentUserId();

        inventarioRepository.modificarObservacion(
                codInventario,
                request.getObservacion(),
                audUsuario
        );

        return buscarPorCodigo(codInventario);
    }

    public void eliminar(int codInventario) {
        log.info("Reversando movimiento de inventario: {}", codInventario);

        int audUsuario = securityUtils.getCurrentUserId();

        inventarioRepository.eliminarMovimiento(codInventario, audUsuario);
    }
}