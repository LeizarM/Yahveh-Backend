package com.yahveh.resource;

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
import org.eclipse.microprofile.jwt.JsonWebToken;

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

    /**
     * POST /api/vistas - Listar todas las vistas (plano)
     */
    @POST
    @RolesAllowed({"admin", "lim"})
    @Path("/menu")
    public Response listarVistas() {
        log.info("POST /api/vistas - Usuario: {}", securityUtils.getCurrentUsername());

        List<VistaResponse> vistas = vistaService.listarTodas( securityUtils.getCurrentUserId() );
        return Response.ok(ApiResponse.success(vistas)).build();
    }


}