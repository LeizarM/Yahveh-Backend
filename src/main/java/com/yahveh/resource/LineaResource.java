package com.yahveh.resource;

import com.yahveh.dto.request.LineaRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.LineaResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.LineaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/api/lineas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"lim", "admin"})
public class LineaResource {

    @Inject
    LineaService lineaService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/lineas - Listar todas las líneas
     */
    @GET
    public Response listarLineas() {
        log.info("GET /api/lineas - Usuario: {}", securityUtils.getCurrentUsername());

        List<LineaResponse> lineas = lineaService.listarTodas();
        return Response.ok(ApiResponse.success(lineas)).build();
    }

    /**
     * GET /api/lineas/{id} - Buscar línea por ID
     */
    @GET
    @Path("/{id}")
    public Response buscarLinea(@PathParam("id") int id) {
        log.info("GET /api/lineas/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        LineaResponse linea = lineaService.buscarPorId(id);
        return Response.ok(ApiResponse.success(linea)).build();
    }

    /**
     * GET /api/lineas/familia/{codFamilia} - Listar líneas por familia
     */
    @GET
    @Path("/familia/{codFamilia}")
    public Response listarPorFamilia(@PathParam("codFamilia") int codFamilia) {
        log.info("GET /api/lineas/familia/{} - Usuario: {}", codFamilia, securityUtils.getCurrentUsername());

        List<LineaResponse> lineas = lineaService.listarPorFamilia(codFamilia);

        return Response.ok(ApiResponse.success(lineas)).build();
    }

    /**
     * GET /api/lineas/buscar?nombre=xxx - Buscar por nombre
     */
    @GET
    @Path("/buscar")
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/lineas/buscar?nombre={} - Usuario: {}", nombre, securityUtils.getCurrentUsername());

        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'nombre' es obligatorio"))
                    .build();
        }

        List<LineaResponse> lineas = lineaService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success(lineas)).build();
    }

    /**
     * POST /api/lineas - Crear nueva línea
     */
    @POST
    public Response crearLinea(@Valid LineaRequest request) {
        int codUsuario =  securityUtils.getCurrentUserId();
        log.info("POST /api/lineas - Usuario: {} - Creando: {}",
                securityUtils.getCurrentUsername(), request.getLinea());

        int nuevoId = lineaService.crearLinea(request, codUsuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Línea creada exitosamente", nuevoId))
                .build();
    }

    /**
     * PUT /api/lineas/{id} - Actualizar línea
     */
    @PUT
    @Path("/{id}")
    public Response actualizarLinea(@PathParam("id") int id, @Valid LineaRequest request) {
        int codUsuario = (int) (long) securityUtils.getCurrentUserId();
        log.info("PUT /api/lineas/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        lineaService.actualizarLinea(id, request, codUsuario);
        return Response.ok(ApiResponse.success("Línea actualizada exitosamente", null)).build();
    }

    /**
     * DELETE /api/lineas/{id} - Eliminar línea
     */
    @DELETE
    @Path("/{id}")
    public Response eliminarLinea(@PathParam("id") int id) {
        int audUsuario = securityUtils.getCurrentUserId();
        log.info("DELETE /api/lineas/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        lineaService.eliminarLinea(id, audUsuario);
        return Response.ok(ApiResponse.success("Línea eliminada exitosamente", null)).build();
    }
}