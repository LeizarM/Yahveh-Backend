package com.yahveh.resource;

import com.yahveh.dto.request.PrecioArticuloRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.PrecioArticuloResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.PrecioArticuloService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/api/precios-articulos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
public class PrecioArticuloResource {

    @Inject
    PrecioArticuloService precioArticuloService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/precios-articulos - Listar todos los precios
     */
    @GET
    public Response listarTodos() {
        log.info("GET /api/precios-articulos - Usuario: {}", securityUtils.getCurrentUsername());

        List<PrecioArticuloResponse> precios = precioArticuloService.listarTodos();

        return Response.ok(ApiResponse.success("Operación exitosa", precios)).build();
    }

    /**
     * GET /api/precios-articulos/{codPrecio} - Buscar precio por ID
     */
    @GET
    @Path("/{codPrecio}")
    public Response buscarPorId(@PathParam("codPrecio") int codPrecio) {
        log.info("GET /api/precios-articulos/{} - Usuario: {}", codPrecio, securityUtils.getCurrentUsername());

        PrecioArticuloResponse precio = precioArticuloService.buscarPorId(codPrecio);

        return Response.ok(ApiResponse.success("Precio encontrado", precio)).build();
    }

    /**
     * GET /api/precios-articulos/articulo/{codArticulo} - Listar precios por artículo
     */
    @GET
    @Path("/articulo/{codArticulo}")
    public Response listarPorArticulo(@PathParam("codArticulo") String codArticulo) {
        log.info("GET /api/precios-articulos/articulo/{} - Usuario: {}", codArticulo, securityUtils.getCurrentUsername());

        List<PrecioArticuloResponse> precios = precioArticuloService.listarPorArticulo(codArticulo);

        return Response.ok(ApiResponse.success("Operación exitosa", precios)).build();
    }

    /**
     * POST /api/precios-articulos - Crear nuevo precio
     */
    @POST
    @RolesAllowed("admin")
    public Response crearPrecio(@Valid PrecioArticuloRequest request) {
        log.info("POST /api/precios-articulos - Usuario: {}", securityUtils.getCurrentUsername());

        int audUsuario =  securityUtils.getCurrentUserId();
        int codPrecio = precioArticuloService.crearPrecio(request, audUsuario);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Precio creado exitosamente", codPrecio))
                .build();
    }

    /**
     * PUT /api/precios-articulos/{codPrecio} - Actualizar precio
     */
    @PUT
    @Path("/{codPrecio}")
    @RolesAllowed("admin")
    public Response actualizarPrecio(
            @PathParam("codPrecio") int codPrecio,
            @Valid PrecioArticuloRequest request) {
        log.info("PUT /api/precios-articulos/{} - Usuario: {}", codPrecio, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        precioArticuloService.actualizarPrecio(codPrecio, request, audUsuario);

        return Response.ok(ApiResponse.success("Precio actualizado exitosamente", null)).build();
    }

    /**
     * PUT /api/precios-articulos/merge - Merge (UPSERT) precio
     * Actualiza si existe (por codArticulo + listaPrecio), inserta si no existe
     */
    @PUT
    @Path("/merge")
    @RolesAllowed("admin")
    public Response mergePrecio(@Valid PrecioArticuloRequest request) {
        log.info("PUT /api/precios-articulos/merge - Usuario: {} - Artículo: {} - Lista: {}",
                securityUtils.getCurrentUsername(),
                request.getCodArticulo(),
                request.getListaPrecio());

        int audUsuario = securityUtils.getCurrentUserId();
        int codPrecio = precioArticuloService.mergePrecio(request, audUsuario);

        return Response.ok(ApiResponse.success("Precio procesado exitosamente", codPrecio)).build();
    }

    /**
     * DELETE /api/precios-articulos/{codPrecio} - Eliminar precio
     */
    @DELETE
    @Path("/{codPrecio}")
    @RolesAllowed("admin")
    public Response eliminarPrecio(@PathParam("codPrecio") int codPrecio) {
        log.info("DELETE /api/precios-articulos/{} - Usuario: {}", codPrecio, securityUtils.getCurrentUsername());

        int audUsuario =  securityUtils.getCurrentUserId();
        precioArticuloService.eliminarPrecio(codPrecio, audUsuario);

        return Response.ok(ApiResponse.success("Precio eliminado exitosamente", null)).build();
    }
}