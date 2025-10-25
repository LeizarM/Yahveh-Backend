package com.yahveh.resource;

import com.yahveh.dto.request.ArticuloRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.ArticuloResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.ArticuloService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Slf4j
@Path("/api/articulos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticuloResource {

    @Inject
    ArticuloService articuloService;
    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/articulos - Listar todos los artículos
     */
    @GET
    @RolesAllowed({ "admin", "lim"})
    public Response listarArticulos() {
        log.info("GET /api/articulos - Usuario: {}", securityUtils.getCurrentUsername());

        List<ArticuloResponse> articulos = articuloService.listarTodos();
        return Response.ok(ApiResponse.success(articulos)).build();
    }

    /**
     * GET /api/articulos/{codigo} - Buscar artículo por código
     */
    @GET
    @Path("/{codigo}")
    @RolesAllowed({"admin", "lim"})
    public Response buscarArticulo(@PathParam("codigo") String codigo) {
        log.info("GET /api/articulos/{} - Usuario: {}", codigo, securityUtils.getCurrentUsername());

        ArticuloResponse articulo = articuloService.buscarPorCodigo(codigo);
        return Response.ok(ApiResponse.success(articulo)).build();
    }

    /**
     * GET /api/articulos/linea/{id} - Listar artículos por línea
     */
    @GET
    @Path("/linea/{id}")
    @RolesAllowed({"lim", "admin"})
    public Response listarPorLinea(@PathParam("id") int id) {
        log.info("GET /api/articulos/linea/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        List<ArticuloResponse> articulos = articuloService.listarPorLinea(id);
        return Response.ok(ApiResponse.success(articulos)).build();
    }

    /**
     * GET /api/articulos/buscar?descripcion=xxx - Buscar por descripción
     */
    @GET
    @Path("/buscar")
    @RolesAllowed({"lim", "admin"})
    public Response buscarPorDescripcion(@QueryParam("descripcion") String descripcion) {
        log.info("GET /api/articulos/buscar?descripcion={} - Usuario: {}", descripcion, securityUtils.getCurrentUsername());

        if (descripcion == null || descripcion.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'descripcion' es requerido"))
                    .build();
        }

        List<ArticuloResponse> articulos = articuloService.buscarPorDescripcion(descripcion);
        return Response.ok(ApiResponse.success(articulos)).build();
    }

    /**
     * POST /api/articulos - Crear nuevo artículo
     */
    @POST
    @RolesAllowed({"lim", "admin"})
    public Response crearArticulo(@Valid ArticuloRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("POST /api/articulos - Usuario: {} - Creando: {}", securityUtils.getCurrentUsername(), request.getCodArticulo());

        String codArticulo = articuloService.crearArticulo(request, codUsuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Artículo creado exitosamente", codArticulo))
                .build();
    }

    /**
     * PUT /api/articulos/{codArticulo} - Actualizar artículo
     */
    @PUT
    @Path("/{codArticulo}")
    @RolesAllowed({"lim", "admin"})
    public Response actualizarArticulo(@PathParam("codArticulo") String codArticulo, @Valid ArticuloRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("PUT /api/articulos/{} - Usuario: {}", codArticulo, securityUtils.getCurrentUsername());

        articuloService.actualizarArticulo(codArticulo, request, codUsuario);
        return Response.ok(ApiResponse.success("Artículo actualizado exitosamente", null)).build();
    }


}