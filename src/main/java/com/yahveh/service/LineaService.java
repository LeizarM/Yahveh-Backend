package com.yahveh.service;

import com.yahveh.dto.request.LineaRequest;
import com.yahveh.dto.response.LineaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Linea;
import com.yahveh.repository.ArticuloRepository;
import com.yahveh.repository.FamiliaRepository;
import com.yahveh.repository.LineaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class LineaService {

    @Inject
    LineaRepository lineaRepository;

    @Inject
    FamiliaRepository familiaRepository;

    @Inject
    ArticuloRepository articuloRepository;

    /**
     * Listar todas las líneas
     */
    public List<LineaResponse> listarTodas() {
        log.info("Listando todas las líneas");
        return lineaRepository.listarTodasCompleto();
    }

    /**
     * Buscar línea por ID
     */
    public LineaResponse buscarPorId(int codLinea) {
        log.info("Buscando línea con ID: {}", codLinea);
        return lineaRepository.buscarPorIdCompleto(codLinea)
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));
    }

    /**
     * Listar líneas por familia
     */
    public List<LineaResponse> listarPorFamilia(int codFamilia) {
        log.info("Listando líneas de familia: {}", codFamilia);

        // Verificar que la familia existe
        if (!familiaRepository.existeFamilia(codFamilia)) {
            throw new NotFoundException("Familia no encontrada");
        }

        return lineaRepository.listarPorFamiliaCompleto(codFamilia);
    }

    /**
     * Buscar líneas por nombre
     */
    public List<LineaResponse> buscarPorNombre(String linea) {
        log.info("Buscando líneas por nombre: {}", linea);
        return lineaRepository.buscarPorNombreCompleto(linea);
    }

    /**
     * Crear nueva línea
     */
    public int crearLinea(LineaRequest request, int audUsuario) {
        log.info("Creando nueva línea: {}", request.getLinea());

        // Verificar que la familia existe
        if (!familiaRepository.existeFamilia(request.getCodFamilia())) {
            throw new NotFoundException("Familia no encontrada");
        }

        Linea linea = Linea.builder()
                .codFamilia(request.getCodFamilia())
                .linea(request.getLinea())
                .audUsuario(audUsuario)
                .build();

        int codLinea = lineaRepository.crearLinea(linea);

        log.info("Línea creada exitosamente con ID: {}", codLinea);
        return codLinea;
    }

    /**
     * Actualizar línea
     */
    public void actualizarLinea(int codLinea, LineaRequest request, int audUsuario) {
        log.info("Actualizando línea ID: {}", codLinea);

        // Verificar que la línea existe
        lineaRepository.buscarPorIdCompleto(codLinea)
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));

        // Verificar que la familia existe
        if (!familiaRepository.existeFamilia(request.getCodFamilia())) {
            throw new NotFoundException("Familia no encontrada");
        }

        Linea linea = Linea.builder()
                .codLinea(codLinea)
                .codFamilia(request.getCodFamilia())
                .linea(request.getLinea())
                .audUsuario(audUsuario)
                .build();

        lineaRepository.actualizarLinea(linea);

        log.info("Línea actualizada exitosamente");
    }

    /**
     * Eliminar línea
     */
    public void eliminarLinea(int codLinea, int audUsuario) {
        log.info("Eliminando línea ID: {}", codLinea);

        // Verificar que la línea existe
        lineaRepository.buscarPorIdCompleto(codLinea)
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));

        // Verificar que no tenga artículos
        var articulos = articuloRepository.listarPorLineaCompleto(codLinea);
        if (!articulos.isEmpty()) {
            throw new BusinessException("No se puede eliminar una línea que tiene artículos asociados");
        }

        lineaRepository.eliminarLinea(codLinea, audUsuario);

        log.info("Línea eliminada exitosamente");
    }
}