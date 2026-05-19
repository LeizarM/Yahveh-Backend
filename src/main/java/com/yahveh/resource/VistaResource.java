package com.yahveh.resource;

import com.yahveh.dto.request.UsuarioVistasRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.VistaResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.VistaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
@Path("/api/vistas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VistaResource {

    @Inject
    VistaService vistaService;

    @Inject
    SecurityUtils securityUtils;

    // -------------------------------------------------------------------------
    // Menú del usuario autenticado
    // -------------------------------------------------------------------------

    /**
     * POST /api/vistas/menu
     * Devuelve las vistas permitidas para el usuario que hace la petición.
     */
    @POST
    @RolesAllowed({"admin", "lim"})
    @Path("/menu")
    public Response listarVistas() {
        log.info("POST /api/vistas/menu - Usuario: {}", securityUtils.getCurrentUsername());
        List<VistaResponse> vistas = vistaService.listarTodas(securityUtils.getCurrentUserId());
        return Response.ok(ApiResponse.success(vistas)).build();
    }

    // -------------------------------------------------------------------------
    // Gestión de permisos (solo admin)
    // -------------------------------------------------------------------------

    /**
     * GET /api/vistas/admin/todas
     * Devuelve TODAS las vistas del sistema (sin filtro de usuario).
     * Solo accesible por administradores.
     */
    @GET
    @RolesAllowed({"admin"})
    @Path("/admin/todas")
    public Response listarTodasAdmin() {
        log.info("GET /api/vistas/admin/todas - Admin: {}", securityUtils.getCurrentUsername());
        List<VistaResponse> vistas = vistaService.listarTodasAdmin();
        return Response.ok(ApiResponse.success(vistas)).build();
    }

    /**
     * GET /api/vistas/admin/usuario/{id}
     * Devuelve las vistas asignadas a un usuario específico.
     * Solo accesible por administradores.
     */
    @GET
    @RolesAllowed({"admin"})
    @Path("/admin/usuario/{id}")
    public Response listarDeUsuario(@PathParam("id") long codUsuario) {
        log.info("GET /api/vistas/admin/usuario/{} - Admin: {}", codUsuario, securityUtils.getCurrentUsername());
        List<VistaResponse> vistas = vistaService.listarDeUsuario(codUsuario);
        return Response.ok(ApiResponse.success(vistas)).build();
    }

    /**
     * PUT /api/vistas/admin/usuario/{id}
     * Reemplaza todas las vistas de un usuario con las proporcionadas.
     * Body: { "codVistas": [1, 2, 5] }
     * Solo accesible por administradores.
     */
    @PUT
    @RolesAllowed({"admin"})
    @Path("/admin/usuario/{id}")
    public Response actualizarVistasDeUsuario(
            @PathParam("id") long codUsuario,
            UsuarioVistasRequest request) {

        log.info("PUT /api/vistas/admin/usuario/{} - Admin: {} - Vistas: {}",
                codUsuario, securityUtils.getCurrentUsername(),
                request != null ? request.getCodVistas() : "null");

        List<Long> codVistas = (request != null && request.getCodVistas() != null)
                ? request.getCodVistas()
                : Collections.emptyList();

        vistaService.actualizarVistasDeUsuario(codUsuario, codVistas);
        return Response.ok(ApiResponse.success("Permisos actualizados correctamente")).build();
    }
}