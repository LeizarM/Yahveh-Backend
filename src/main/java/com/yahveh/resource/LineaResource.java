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
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Slf4j
@Path("/api/lineas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LineaResource {

    @Inject
    LineaService lineaService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/lineas - Listar todas las líneas
     */
    @GET
    @RolesAllowed({"lim", "admin" })
    public Response listarLineas() {
        log.info("GET /api/lineas - Usuario");

        List<LineaResponse> lineas = lineaService.listarTodas();
        return Response.ok(ApiResponse.success(lineas)).build();
    }

    /**
     * GET /api/lineas/{id} - Buscar línea por ID
     */
    @GET
    @Path("/{id}")
    @RolesAllowed({"lim", "admin", })
    public Response buscarLinea(@PathParam("id") int id) {
        log.info("GET /api/lineas/{} - Usuario");

        LineaResponse linea = lineaService.buscarPorId(id);
        return Response.ok(ApiResponse.success(linea)).build();
    }

    /**
     * POST /api/lineas - Crear nueva línea
     */
    @POST
    @RolesAllowed({"lim", "admin"})
    public Response crearLinea(@Valid LineaRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("POST /api/lineas - Usuario: {} - Creando: {}", securityUtils.getCurrentUsername(), request.getLinea());

        Long nuevoId = lineaService.crearLinea(request, codUsuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Línea creada exitosamente", nuevoId))
                .build();
    }

    /**
     * PUT /api/lineas/{id} - Actualizar línea
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed({"lim", "admin"})
    public Response actualizarLinea(@PathParam("id") int id, @Valid LineaRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("PUT /api/lineas/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        lineaService.actualizarLinea(id, request, codUsuario);
        return Response.ok(ApiResponse.success("Línea actualizada exitosamente", null)).build();
    }


}