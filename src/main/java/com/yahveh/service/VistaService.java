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
     * Listar todas las vistas (plano)
     */
    public List<VistaResponse> listarTodas() {
        log.info("Listando todas las vistas");

        return vistaRepository.listarTodas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convertir Vista a VistaResponse
     */
    private VistaResponse toResponse(Vista vista) {
        return VistaResponse.builder()
                .codVista(vista.getCodVista())
                .codVistaPadre(vista.getCodVistaPadre())
                .direccion(vista.getDireccion())
                .titulo(vista.getTitulo())
                .build();
    }


}