package com.yahveh.service;

import com.yahveh.dto.request.PrecioArticuloRequest;
import com.yahveh.dto.response.PrecioArticuloResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.PrecioArticulo;
import com.yahveh.repository.PrecioArticuloRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class PrecioArticuloService {

    @Inject
    PrecioArticuloRepository precioArticuloRepository;

    /**
     * Listar todos los precios
     */
    public List<PrecioArticuloResponse> listarTodos() {
        log.info("Listando todos los precios de artículos");
        return precioArticuloRepository.listarTodosCompleto();
    }

    /**
     * Buscar precio por ID
     */
    public PrecioArticuloResponse buscarPorId(int codPrecio) {
        log.info("Buscando precio con ID: {}", codPrecio);
        return precioArticuloRepository.buscarPorIdCompleto(codPrecio)
                .orElseThrow(() -> new NotFoundException("Precio no encontrado"));
    }

    /**
     * Listar precios por artículo
     */
    public List<PrecioArticuloResponse> listarPorArticulo(String codArticulo) {
        log.info("Listando precios del artículo: {}", codArticulo);
        return precioArticuloRepository.listarPorArticulo(codArticulo);
    }

    /**
     * Crear nuevo precio
     */
    public int crearPrecio(PrecioArticuloRequest request, int audUsuario) {
        log.info("Creando nuevo precio para artículo: {}", request.getCodArticulo());

        PrecioArticulo precio = PrecioArticulo.builder()
                .codArticulo(request.getCodArticulo())
                .listaPrecio(request.getListaPrecio())
                .precioBase(request.getPrecioBase())
                .precio(request.getPrecio())
                .precioSinFactura(request.getPrecioSinFactura())
                .audUsuario(audUsuario)
                .build();

        int codPrecio = precioArticuloRepository.crearPrecio(precio);

        log.info("Precio creado exitosamente con ID: {}", codPrecio);
        return codPrecio;
    }

    /**
     * Actualizar precio
     */
    public void actualizarPrecio(int codPrecio, PrecioArticuloRequest request, int audUsuario) {
        log.info("Actualizando precio: {}", codPrecio);

        // Verificar que el precio existe
        precioArticuloRepository.buscarPorIdCompleto(codPrecio)
                .orElseThrow(() -> new NotFoundException("Precio no encontrado"));

        PrecioArticulo precio = PrecioArticulo.builder()
                .codPrecio(codPrecio)
                .codArticulo(request.getCodArticulo())
                .listaPrecio(request.getListaPrecio())
                .precioBase(request.getPrecioBase())
                .precio(request.getPrecio())
                .precioSinFactura(request.getPrecioSinFactura())
                .audUsuario(audUsuario)
                .build();

        precioArticuloRepository.actualizarPrecio(precio);

        log.info("Precio actualizado exitosamente");
    }

    /**
     * Merge (UPSERT) precio - Actualiza si existe, inserta si no existe
     */
    public int mergePrecio(PrecioArticuloRequest request, int audUsuario) {
        log.info("Procesando merge de precio para artículo: {} - Lista: {}",
                request.getCodArticulo(), request.getListaPrecio());

        PrecioArticulo precio = PrecioArticulo.builder()
                .codArticulo(request.getCodArticulo())
                .listaPrecio(request.getListaPrecio())
                .precioBase(request.getPrecioBase())
                .precio(request.getPrecio())
                .precioSinFactura(request.getPrecioSinFactura())
                .audUsuario(audUsuario)
                .build();

        int codPrecio = precioArticuloRepository.mergePrecio(precio);

        log.info("Precio procesado exitosamente (merge) con ID: {}", codPrecio);
        return codPrecio;
    }

    /**
     * Eliminar precio
     */
    public void eliminarPrecio(int codPrecio, int audUsuario) {
        log.info("Eliminando precio: {}", codPrecio);

        // Verificar que el precio existe
        precioArticuloRepository.buscarPorIdCompleto(codPrecio)
                .orElseThrow(() -> new NotFoundException("Precio no encontrado"));

        precioArticuloRepository.eliminarPrecio(codPrecio, audUsuario);

        log.info("Precio eliminado exitosamente");
    }
}