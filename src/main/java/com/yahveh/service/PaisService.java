package com.yahveh.service;

import com.yahveh.dto.request.PaisRequest;
import com.yahveh.dto.response.PaisResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Pais;
import com.yahveh.repository.PaisRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class PaisService {

    @Inject
    PaisRepository paisRepository;

    /**
     * Listar todos los países
     */
    public List<PaisResponse> listarTodos() {
        log.info("Listando todos los países");
        return paisRepository.listarTodosCompleto();
    }

    /**
     * Buscar país por ID
     */
    public PaisResponse buscarPorId(int codPais) {
        log.info("Buscando país con ID: {}", codPais);
        return paisRepository.buscarPorIdCompleto(codPais)
                .orElseThrow(() -> new NotFoundException("País no encontrado"));
    }

    /**
     * Buscar países por nombre
     */
    public List<PaisResponse> buscarPorNombre(String pais) {
        log.info("Buscando países por nombre: {}", pais);
        return paisRepository.buscarPorNombreCompleto(pais);
    }

    /**
     * Crear nuevo país
     */
    public int crearPais(PaisRequest request, int audUsuario) {
        log.info("Creando nuevo país: {}", request.getPais());

        Pais pais = Pais.builder()
                .pais(request.getPais())
                .audUsuario(audUsuario)
                .build();

        int codPais = paisRepository.crearPais(pais);

        log.info("País creado exitosamente con ID: {}", codPais);
        return codPais;
    }

    /**
     * Actualizar país
     */
    public void actualizarPais(int codPais, PaisRequest request, int audUsuario) {
        log.info("Actualizando país: {}", codPais);

        // Verificar que el país existe
        paisRepository.buscarPorIdCompleto(codPais)
                .orElseThrow(() -> new NotFoundException("País no encontrado"));

        Pais pais = Pais.builder()
                .codPais(codPais)
                .pais(request.getPais())
                .audUsuario(audUsuario)
                .build();

        paisRepository.actualizarPais(pais);

        log.info("País actualizado exitosamente");
    }

    /**
     * Eliminar país
     */
    public void eliminarPais(int codPais, int audUsuario) {
        log.info("Eliminando país: {}", codPais);

        // Verificar que el país existe
        paisRepository.buscarPorIdCompleto(codPais)
                .orElseThrow(() -> new NotFoundException("País no encontrado"));

        paisRepository.eliminarPais(codPais, audUsuario);

        log.info("País eliminado exitosamente");
    }
}