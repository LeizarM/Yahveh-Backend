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
     * Listar artículos por línea
     */
    public List<ArticuloResponse> listarPorLinea(int codLinea) {
        log.info("Listando artículos de línea: {}", codLinea);



        return articuloRepository.listarPorLineaCompleto(codLinea);
    }



    /**
     * Crear nuevo artículo
     */
    public String crearArticulo(ArticuloRequest request, int audUsuario) {  // Retorna String
        log.info("Creando nuevo artículo: {}", request.getCodArticulo());

        Articulo articulo = Articulo.builder()
                .codArticulo(request.getCodArticulo())  // Puede ser NULL para auto-generar
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
    public void eliminarArticulo(String codArticulo, int audUsuario) {
        log.info("Eliminando artículo: {}", codArticulo);



        articuloRepository.eliminarArticulo(codArticulo, audUsuario);

        log.info("Artículo eliminado exitosamente");
    }
}