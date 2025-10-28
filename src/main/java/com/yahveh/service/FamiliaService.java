package com.yahveh.service;

import com.yahveh.dto.request.FamiliaRequest;
import com.yahveh.dto.response.FamiliaResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Familia;
import com.yahveh.repository.FamiliaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class FamiliaService {

    @Inject
    FamiliaRepository familiaRepository;

    /**
     * Listar todas las familias
     */
    public List<FamiliaResponse> listarTodas() {
        log.info("Listando todas las familias");
        return familiaRepository.listarTodasCompleto();
    }

    /**
     * Buscar familia por ID
     */
    public FamiliaResponse buscarPorId(int codFamilia) {
        log.info("Buscando familia con ID: {}", codFamilia);
        return familiaRepository.buscarPorIdCompleto(codFamilia)
                .orElseThrow(() -> new NotFoundException("Familia no encontrada"));
    }

    /**
     * Buscar familias por nombre
     */
    public List<FamiliaResponse> buscarPorNombre(String familia) {
        log.info("Buscando familias por nombre: {}", familia);
        return familiaRepository.buscarPorNombreCompleto(familia);
    }

    /**
     * Crear nueva familia
     */
    public int crearFamilia(FamiliaRequest request, int audUsuario) {
        log.info("Creando nueva familia: {}", request.getFamilia());

        Familia familia = Familia.builder()
                .familia(request.getFamilia())
                .audUsuario(audUsuario)
                .build();

        int codFamilia = familiaRepository.crearFamilia(familia);

        log.info("Familia creada exitosamente con ID: {}", codFamilia);
        return codFamilia;
    }

    /**
     * Actualizar familia
     */
    public void actualizarFamilia(int codFamilia, FamiliaRequest request, int audUsuario) {
        log.info("Actualizando familia: {}", codFamilia);

        // Verificar que la familia existe
        familiaRepository.buscarPorIdCompleto(codFamilia)
                .orElseThrow(() -> new NotFoundException("Familia no encontrada"));

        Familia familia = Familia.builder()
                .codFamilia(codFamilia)
                .familia(request.getFamilia())
                .audUsuario(audUsuario)
                .build();

        familiaRepository.actualizarFamilia(familia);

        log.info("Familia actualizada exitosamente");
    }

    /**
     * Eliminar familia
     */
    public void eliminarFamilia(int codFamilia, int audUsuario) {
        log.info("Eliminando familia: {}", codFamilia);

        // Verificar que la familia existe
        familiaRepository.buscarPorIdCompleto(codFamilia)
                .orElseThrow(() -> new NotFoundException("Familia no encontrada"));

        familiaRepository.eliminarFamilia(codFamilia, audUsuario);

        log.info("Familia eliminada exitosamente");
    }
}