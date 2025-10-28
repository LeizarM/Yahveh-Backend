package com.yahveh.resource;

import com.yahveh.dto.request.FamiliaRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.FamiliaResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.FamiliaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/api/familias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"lim", "admin"})
public class FamiliaResource {

    @Inject
    FamiliaService familiaService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/familias - Listar todas las familias
     */
    @GET
    public Response listarTodas() {
        log.info("GET /api/familias - Usuario: {}", securityUtils.getCurrentUsername());

        List<FamiliaResponse> familias = familiaService.listarTodas();

        return Response.ok(ApiResponse.success(familias)).build();
    }

    /**
     * GET /api/familias/{codFamilia} - Buscar familia por ID
     */
    @GET
    @Path("/{codFamilia}")
    public Response buscarPorId(@PathParam("codFamilia") int codFamilia) {
        log.info("GET /api/familias/{} - Usuario: {}", codFamilia, securityUtils.getCurrentUsername());

        FamiliaResponse familia = familiaService.buscarPorId(codFamilia);

        return Response.ok(ApiResponse.success(familia)).build();
    }

    /**
     * GET /api/familias/buscar?nombre=xxx - Buscar por nombre
     */
    @GET
    @Path("/buscar")
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/familias/buscar?nombre={} - Usuario: {}", nombre, securityUtils.getCurrentUsername());

        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'nombre' es obligatorio"))
                    .build();
        }

        List<FamiliaResponse> familias = familiaService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success(familias)).build();
    }

    /**
     * POST /api/familias - Crear nueva familia
     */
    @POST
    public Response crearFamilia(@Valid FamiliaRequest request) {
        log.info("POST /api/familias - Usuario: {} - Creando: {}",
                securityUtils.getCurrentUsername(), request.getFamilia());

        int audUsuario = securityUtils.getCurrentUserId();
        int codFamilia = familiaService.crearFamilia(request, audUsuario);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Familia creada exitosamente", codFamilia))
                .build();
    }

    /**
     * PUT /api/familias/{codFamilia} - Actualizar familia
     */
    @PUT
    @Path("/{codFamilia}")
    public Response actualizarFamilia(
            @PathParam("codFamilia") int codFamilia,
            @Valid FamiliaRequest request) {
        log.info("PUT /api/familias/{} - Usuario: {}", codFamilia, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        familiaService.actualizarFamilia(codFamilia, request, audUsuario);

        return Response.ok(ApiResponse.success("Familia actualizada exitosamente", null)).build();
    }

    /**
     * DELETE /api/familias/{codFamilia} - Eliminar familia
     */
    @DELETE
    @Path("/{codFamilia}")
    public Response eliminarFamilia(@PathParam("codFamilia") int codFamilia) {
        log.info("DELETE /api/familias/{} - Usuario: {}", codFamilia, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        familiaService.eliminarFamilia(codFamilia, audUsuario);

        return Response.ok(ApiResponse.success("Familia eliminada exitosamente", null)).build();
    }
}