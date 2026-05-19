package com.yahveh.service;

import com.yahveh.dto.request.ReglaDescuentoRequest;
import com.yahveh.dto.response.ReglaDescuentoResponse;
import com.yahveh.repository.ReglaDescuentoRepository;
import com.yahveh.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ApplicationScoped
@Slf4j
public class ReglaDescuentoService {

    @Inject
    ReglaDescuentoRepository reglaDescuentoRepository;

    @Inject
    SecurityUtils securityUtils;

    public List<ReglaDescuentoResponse> listarTodas() {
        log.info("Listando todas las reglas de descuento");
        return reglaDescuentoRepository.listarTodas();
    }

    public ReglaDescuentoResponse buscarPorArticulo(String codArticulo) {
        log.info("Buscando regla de descuento para artículo: {}", codArticulo);
        return reglaDescuentoRepository.buscarPorArticulo(codArticulo)
                .orElseThrow(() -> new NotFoundException("No hay regla de descuento para este artículo"));
    }

    public ReglaDescuentoResponse crear(ReglaDescuentoRequest request) {
        log.info("Creando regla de descuento para artículo: {}", request.getCodArticulo());
        int audUsuario = securityUtils.getCurrentUserId();
        long id = reglaDescuentoRepository.crear(
                request.getCodArticulo(),
                request.getCantidadMinima(),
                request.getDescuento(),
                audUsuario
        );
        return reglaDescuentoRepository.buscarPorCodigo(id)
                .orElseThrow(() -> new RuntimeException("Error al recuperar regla creada"));
    }

    public ReglaDescuentoResponse actualizar(long codReglaDescuento, ReglaDescuentoRequest request) {
        log.info("Actualizando regla de descuento: {}", codReglaDescuento);
        int audUsuario = securityUtils.getCurrentUserId();
        reglaDescuentoRepository.actualizar(
                codReglaDescuento,
                request.getCantidadMinima(),
                request.getDescuento(),
                audUsuario
        );
        return reglaDescuentoRepository.buscarPorCodigo(codReglaDescuento)
                .orElseThrow(() -> new NotFoundException("Regla no encontrada"));
    }

    public void eliminar(long codReglaDescuento) {
        log.info("Eliminando regla de descuento: {}", codReglaDescuento);
        int audUsuario = securityUtils.getCurrentUserId();
        reglaDescuentoRepository.eliminar(codReglaDescuento, audUsuario);
    }
}
