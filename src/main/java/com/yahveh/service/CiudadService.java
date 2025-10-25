package com.yahveh.service;

import com.yahveh.dto.request.CiudadRequest;
import com.yahveh.dto.response.CiudadResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Ciudad;
import com.yahveh.repository.CiudadRepository;
import com.yahveh.repository.PaisRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class CiudadService {

    @Inject
    CiudadRepository ciudadRepository;

    @Inject
    PaisRepository paisRepository;

    /**
     * Listar todas las ciudades
     */
    public List<CiudadResponse> listarTodas() {
        log.info("Listando todas las ciudades");
        return ciudadRepository.listarTodasCompleto();
    }

    /**
     * Buscar ciudad por ID
     */
    public CiudadResponse buscarPorId(int codCiudad) {
        log.info("Buscando ciudad con ID: {}", codCiudad);
        return ciudadRepository.buscarPorIdCompleto(codCiudad)
                .orElseThrow(() -> new NotFoundException("Ciudad no encontrada"));
    }

    /**
     * Listar ciudades por país
     */
    public List<CiudadResponse> listarPorPais(int codPais) {
        log.info("Listando ciudades de país: {}", codPais);

        // Verificar que el país existe
        if (!paisRepository.existePais(codPais)) {
            throw new NotFoundException("País no encontrado");
        }

        return ciudadRepository.listarPorPaisCompleto(codPais);
    }

    /**
     * Buscar ciudades por nombre
     */
    public List<CiudadResponse> buscarPorNombre(String ciudad) {
        log.info("Buscando ciudades por nombre: {}", ciudad);
        return ciudadRepository.buscarPorNombreCompleto(ciudad);
    }

    /**
     * Crear nueva ciudad
     */
    public int crearCiudad(CiudadRequest request, int audUsuario) {
        log.info("Creando nueva ciudad: {}", request.getCiudad());

        // Verificar que el país existe
        if (!paisRepository.existePais(request.getCodPais())) {
            throw new NotFoundException("País no encontrado");
        }

        Ciudad ciudad = Ciudad.builder()
                .codPais(request.getCodPais())
                .ciudad(request.getCiudad())
                .audUsuario(audUsuario)
                .build();

        int codCiudad = ciudadRepository.crearCiudad(ciudad);

        log.info("Ciudad creada exitosamente con ID: {}", codCiudad);
        return codCiudad;
    }

    /**
     * Actualizar ciudad
     */
    public void actualizarCiudad(int codCiudad, CiudadRequest request, int audUsuario) {
        log.info("Actualizando ciudad: {}", codCiudad);

        // Verificar que la ciudad existe
        ciudadRepository.buscarPorIdCompleto(codCiudad)
                .orElseThrow(() -> new NotFoundException("Ciudad no encontrada"));

        // Verificar que el país existe
        if (!paisRepository.existePais(request.getCodPais())) {
            throw new NotFoundException("País no encontrado");
        }

        Ciudad ciudad = Ciudad.builder()
                .codCiudad(codCiudad)
                .codPais(request.getCodPais())
                .ciudad(request.getCiudad())
                .audUsuario(audUsuario)
                .build();

        ciudadRepository.actualizarCiudad(ciudad);

        log.info("Ciudad actualizada exitosamente");
    }

    /**
     * Eliminar ciudad
     */
    public void eliminarCiudad(int codCiudad, int audUsuario) {
        log.info("Eliminando ciudad: {}", codCiudad);

        // Verificar que la ciudad existe
        ciudadRepository.buscarPorIdCompleto(codCiudad)
                .orElseThrow(() -> new NotFoundException("Ciudad no encontrada"));

        ciudadRepository.eliminarCiudad(codCiudad, audUsuario);

        log.info("Ciudad eliminada exitosamente");
    }
}