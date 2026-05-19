package com.yahveh.service;

import com.yahveh.dto.response.VistaResponse;
import com.yahveh.model.Vista;
import com.yahveh.repository.VistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class VistaService {

    @Inject
    VistaRepository vistaRepository;

    /**
     * Menú dinámico del usuario autenticado (solo vistas permitidas)
     */
    public List<VistaResponse> listarTodas(long codUsuario) {
        log.info("Listando vistas del usuario {}", codUsuario);
        return vistaRepository.listarTodas(codUsuario).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Admin: listar TODAS las vistas del sistema (sin filtro)
     */
    public List<VistaResponse> listarTodasAdmin() {
        log.info("Listando todas las vistas del sistema (admin)");
        return vistaRepository.listarTodasAdmin().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Admin: listar las vistas asignadas a un usuario específico
     */
    public List<VistaResponse> listarDeUsuario(long codUsuario) {
        log.info("Listando vistas del usuario {} (admin)", codUsuario);
        return vistaRepository.listarDeUsuario(codUsuario).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Admin: reemplazar todas las vistas de un usuario
     */
    public void actualizarVistasDeUsuario(long codUsuario, List<Long> codVistas) {
        log.info("Actualizando permisos del usuario {}: {} vistas", codUsuario, codVistas.size());
        vistaRepository.actualizarVistasDeUsuario(codUsuario, codVistas);
    }

    private VistaResponse toResponse(Vista vista) {
        return VistaResponse.builder()
                .codVista(vista.getCodVista())
                .codVistaPadre(vista.getCodVistaPadre())
                .direccion(vista.getDireccion())
                .titulo(vista.getTitulo())
                .build();
    }
}