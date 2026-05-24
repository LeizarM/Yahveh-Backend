package com.yahveh.resource;

import com.yahveh.dto.request.ArticuloRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.ArticuloResponse;
import com.yahveh.dto.response.PagedResponse;
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
     * GET /api/articulos - Listar artículos (paginado server-side).
     *
     * Query params:
     *   - page (1-based, default 1)
     *   - pageSize (default 20, max 500)
     *   - search (texto libre - busca en código, descripción, descripción2, línea)
     *
     * Respuesta: ApiResponse<PagedResponse<ArticuloResponse>>
     *   { data: [...], total, page, pageSize, totalPages }
     */
    @GET
    @RolesAllowed({ "admin", "lim"})
    public Response listarArticulos(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("20") int pageSize,
            @QueryParam("search") String search) {
        log.info("GET /api/articulos?page={}&pageSize={}&search={} - Usuario: {}",
                page, pageSize, search, securityUtils.getCurrentUsername());

        PagedResponse<ArticuloResponse> paged = articuloService.listarPaginado(search, page, pageSize);
        return Response.ok(ApiResponse.success(paged)).build();
    }

    /**
     * GET /api/articulos/todos - Listar TODOS los artículos sin paginar (legacy).
     * Útil para combos/dropdowns donde se necesita la lista completa.
     */
    @GET
    @Path("/todos")
    @RolesAllowed({ "admin", "lim"})
    public Response listarTodosArticulos() {
        log.info("GET /api/articulos/todos - Usuario: {}", securityUtils.getCurrentUsername());
        List<ArticuloResponse> articulos = articuloService.listarTodos();
        return Response.ok(ApiResponse.success(articulos)).build();
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
     * POST /api/articulos - Crear nuevo artículo (solo admin)
     */
    @POST
    @RolesAllowed({"admin"})
    public Response crearArticulo(@Valid ArticuloRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("POST /api/articulos - Usuario: {} - Creando: {}",
                securityUtils.getCurrentUsername(),
                request.getCodArticulo());

        String codArticulo = articuloService.crearArticulo(request, codUsuario);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Artículo creado exitosamente", codArticulo))
                .build();
    }

    /**
     * PUT /api/articulos/{codArticulo} - Actualizar artículo (solo admin)
     */
    @PUT
    @Path("/{codArticulo}")
    @RolesAllowed({"admin"})
    public Response actualizarArticulo(@PathParam("codArticulo") String codArticulo, @Valid ArticuloRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("PUT /api/articulos/{} - Usuario: {}", codArticulo, securityUtils.getCurrentUsername());

        articuloService.actualizarArticulo(codArticulo, request, codUsuario);
        return Response.ok(ApiResponse.success("Artículo actualizado exitosamente", null)).build();
    }


}