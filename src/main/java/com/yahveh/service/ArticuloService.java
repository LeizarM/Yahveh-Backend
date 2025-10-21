package com.yahveh.service;

import com.yahveh.dto.request.ArticuloRequest;
import com.yahveh.dto.response.ArticuloResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Articulo;
import com.yahveh.repository.ArticuloRepository;
import com.yahveh.repository.LineaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class ArticuloService {

    @Inject
    ArticuloRepository articuloRepository;

    @Inject
    LineaRepository lineaRepository;

    /**
     * Listar todos los artículos
     */
    public List<ArticuloResponse> listarTodos() {
        log.info("Listando todos los artículos");
        return articuloRepository.listarTodosCompleto();
    }

    /**
     * Buscar artículo por código
     */
    public ArticuloResponse buscarPorCodigo(String codArticulo) {
        log.info("Buscando artículo con código: {}", codArticulo);
        return articuloRepository.buscarPorCodigoCompleto(codArticulo)
                .orElseThrow(() -> new NotFoundException("Artículo no encontrado"));
    }

    /**
     * Listar artículos por línea
     */
    public List<ArticuloResponse> listarPorLinea(int codLinea) {
        log.info("Listando artículos de línea: {}", codLinea);

        // Verificar que la línea existe
        lineaRepository.buscarPorId(codLinea)
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));

        return articuloRepository.listarPorLineaCompleto(codLinea);
    }

    /**
     * Buscar artículos por descripción
     */
    public List<ArticuloResponse> buscarPorDescripcion(String descripcion) {
        log.info("Buscando artículos por descripción: {}", descripcion);
        return articuloRepository.buscarPorDescripcionCompleto(descripcion);
    }

    /**
     * Crear nuevo artículo
     */
    public String crearArticulo(ArticuloRequest request, int audUsuario) {
        log.info("Creando nuevo artículo: {}", request.getCodArticulo());

        // Verificar que la línea existe
        lineaRepository.buscarPorId(request.getCodLinea())
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));

        // Verificar que el código no exista
        if (articuloRepository.existeArticulo(request.getCodArticulo())) {
            throw new BusinessException("El código de artículo ya existe");
        }

        Articulo articulo = Articulo.builder()
                .codArticulo(request.getCodArticulo().toUpperCase().trim())
                .codLinea(request.getCodLinea())
                .descripcion(request.getDescripcion())
                .descripcion2(request.getDescripcion2())
                .audUsuario(audUsuario)
                .build();

        String codArticulo = articuloRepository.crearArticulo(articulo);

        log.info("Artículo creado exitosamente: {}", codArticulo);
        return codArticulo;
    }

    /**
     * Actualizar artículo
     */
    public void actualizarArticulo(String codArticulo, ArticuloRequest request, int audUsuario) {
        log.info("Actualizando artículo: {}", codArticulo);

        // Verificar que el artículo existe
        articuloRepository.buscarPorCodigoCompleto(codArticulo)
                .orElseThrow(() -> new NotFoundException("Artículo no encontrado"));

        // Verificar que la línea existe
        lineaRepository.buscarPorId(request.getCodLinea())
                .orElseThrow(() -> new NotFoundException("Línea no encontrada"));

        Articulo articulo = Articulo.builder()
                .codArticulo(codArticulo.toUpperCase().trim())
                .codLinea(request.getCodLinea())
                .descripcion(request.getDescripcion())
                .descripcion2(request.getDescripcion2())
                .audUsuario(audUsuario)
                .build();

        articuloRepository.actualizarArticulo(articulo);

        log.info("Artículo actualizado exitosamente");
    }

    /**
     * Eliminar artículo
     */
    public void eliminarArticulo(String codArticulo, Long audUsuario) {
        log.info("Eliminando artículo: {}", codArticulo);

        // Verificar que el artículo existe
        articuloRepository.buscarPorCodigoCompleto(codArticulo)
                .orElseThrow(() -> new NotFoundException("Artículo no encontrado"));

        articuloRepository.eliminarArticulo(codArticulo, audUsuario);

        log.info("Artículo eliminado exitosamente");
    }
}