package com.yahveh.service;

import com.yahveh.dto.request.ZonaRequest;
import com.yahveh.dto.response.ZonaResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Zona;
import com.yahveh.repository.CiudadRepository;
import com.yahveh.repository.ZonaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class ZonaService {

    @Inject
    ZonaRepository zonaRepository;

    @Inject
    CiudadRepository ciudadRepository;

    /**
     * Listar todas las zonas
     */
    public List<ZonaResponse> listarTodas() {
        log.info("Listando todas las zonas");
        return zonaRepository.listarTodasCompleto();
    }

    /**
     * Buscar zona por ID
     */
    public ZonaResponse buscarPorId(int codZona) {
        log.info("Buscando zona con ID: {}", codZona);
        return zonaRepository.buscarPorIdCompleto(codZona)
                .orElseThrow(() -> new NotFoundException("Zona no encontrada"));
    }

    /**
     * Listar zonas por ciudad
     */
    public List<ZonaResponse> listarPorCiudad(int codCiudad) {
        log.info("Listando zonas de ciudad: {}", codCiudad);

        // Verificar que la ciudad existe
        if (!ciudadRepository.existeCiudad(codCiudad)) {
            throw new NotFoundException("Ciudad no encontrada");
        }

        return zonaRepository.listarPorCiudadCompleto(codCiudad);
    }

    /**
     * Buscar zonas por nombre
     */
    public List<ZonaResponse> buscarPorNombre(String zona) {
        log.info("Buscando zonas por nombre: {}", zona);
        return zonaRepository.buscarPorNombreCompleto(zona);
    }

    /**
     * Crear nueva zona
     */
    public int crearZona(ZonaRequest request, int audUsuario) {
        log.info("Creando nueva zona: {}", request.getZona());

        // Verificar que la ciudad existe
        if (!ciudadRepository.existeCiudad(request.getCodCiudad())) {
            throw new NotFoundException("Ciudad no encontrada");
        }

        Zona zona = Zona.builder()
                .codCiudad(request.getCodCiudad())
                .zona(request.getZona())
                .audUsuario(audUsuario)
                .build();

        int codZona = zonaRepository.crearZona(zona);

        log.info("Zona creada exitosamente con ID: {}", codZona);
        return codZona;
    }

    /**
     * Actualizar zona
     */
    public void actualizarZona(int codZona, ZonaRequest request, int audUsuario) {
        log.info("Actualizando zona: {}", codZona);

        // Verificar que la zona existe
        zonaRepository.buscarPorIdCompleto(codZona)
                .orElseThrow(() -> new NotFoundException("Zona no encontrada"));

        // Verificar que la ciudad existe
        if (!ciudadRepository.existeCiudad(request.getCodCiudad())) {
            throw new NotFoundException("Ciudad no encontrada");
        }

        Zona zona = Zona.builder()
                .codZona(codZona)
                .codCiudad(request.getCodCiudad())
                .zona(request.getZona())
                .audUsuario(audUsuario)
                .build();

        zonaRepository.actualizarZona(zona);

        log.info("Zona actualizada exitosamente");
    }

    /**
     * Eliminar zona
     */
    public void eliminarZona(int codZona, int audUsuario) {
        log.info("Eliminando zona: {}", codZona);

        // Verificar que la zona existe
        zonaRepository.buscarPorIdCompleto(codZona)
                .orElseThrow(() -> new NotFoundException("Zona no encontrada"));

        zonaRepository.eliminarZona(codZona, audUsuario);

        log.info("Zona eliminada exitosamente");
    }
}