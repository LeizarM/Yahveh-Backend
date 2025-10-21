package com.yahveh.service;

import com.yahveh.dto.request.LineaRequest;
import com.yahveh.dto.response.LineaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Linea;
import com.yahveh.repository.ArticuloRepository;
import com.yahveh.repository.LineaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class LineaService {

    @Inject
    LineaRepository lineaRepository;

    @Inject
    ArticuloRepository articuloRepository;

    public List<LineaResponse> listarTodas() {
        log.info("Listando todas las líneas");

        return lineaRepository.listarTodas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public LineaResponse buscarPorId(int codLinea) {
        log.info("Buscando línea con ID: {}", codLinea);

        return lineaRepository.buscarPorId(codLinea)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));
    }

    public Long crearLinea(LineaRequest request, int audUsuario) {
        log.info("Creando nueva línea: {}", request.getLinea());

        if (!request.esCreacion()) {
            throw new BusinessException("No se debe enviar codLinea al crear");
        }

        Linea linea = Linea.builder()
                .linea(request.getLinea())
                .audUsuario(audUsuario)
                .build();

        Long codLinea = lineaRepository.crearLinea(linea);

        log.info("Línea creada exitosamente con ID: {}", codLinea);
        return codLinea;
    }

    public void actualizarLinea(int codLinea, LineaRequest request, int audUsuario) {
        log.info("Actualizando línea ID: {}", codLinea);

        lineaRepository.buscarPorId(codLinea)
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));

        Linea linea = Linea.builder()
                .codLinea(codLinea)
                .linea(request.getLinea())
                .audUsuario(audUsuario)
                .build();

        lineaRepository.actualizarLinea(linea);

        log.info("Línea actualizada exitosamente");
    }

    public void eliminarLinea(int codLinea, Long audUsuario) {
        log.info("Eliminando línea ID: {}", codLinea);

        lineaRepository.buscarPorId(codLinea)
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));

        // Verificar que no tenga artículos
        var articulos = articuloRepository.listarPorLineaCompleto(codLinea);
        if (!articulos.isEmpty()) {
            throw new BusinessException("No se puede eliminar una línea que tiene artículos asociados");
        }

        lineaRepository.eliminarLinea(codLinea, audUsuario);

        log.info("Línea eliminada exitosamente");
    }

    private LineaResponse toResponse(Linea linea) {
        // Contar artículos de esta línea
        int totalArticulos = articuloRepository.listarPorLineaCompleto(linea.getCodLinea()).size();

        return LineaResponse.builder()
                .codLinea(linea.getCodLinea())
                .linea(linea.getLinea())
                .totalArticulos(totalArticulos)
                .build();
    }
}