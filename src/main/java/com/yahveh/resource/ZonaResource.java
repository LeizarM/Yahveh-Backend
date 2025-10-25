package com.yahveh.resource;

import com.yahveh.dto.request.ZonaRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.ZonaResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.ZonaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/api/zonas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
public class ZonaResource {

    @Inject
    ZonaService zonaService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/zonas - Listar todas las zonas
     */
    @GET
    public Response listarTodas() {
        log.info("GET /api/zonas - Usuario: {}", securityUtils.getCurrentUsername());

        List<ZonaResponse> zonas = zonaService.listarTodas();

        return Response.ok(ApiResponse.success("Operación exitosa", zonas)).build();
    }

    /**
     * GET /api/zonas/{codZona} - Buscar zona por ID
     */
    @GET
    @Path("/{codZona}")
    public Response buscarPorId(@PathParam("codZona") int codZona) {
        log.info("GET /api/zonas/{} - Usuario: {}", codZona, securityUtils.getCurrentUsername());

        ZonaResponse zona = zonaService.buscarPorId(codZona);

        return Response.ok(ApiResponse.success("Zona encontrada", zona)).build();
    }

    /**
     * GET /api/zonas/ciudad/{codCiudad} - Listar zonas por ciudad
     */
    @GET
    @Path("/ciudad/{codCiudad}")
    public Response listarPorCiudad(@PathParam("codCiudad") int codCiudad) {
        log.info("GET /api/zonas/ciudad/{} - Usuario: {}", codCiudad, securityUtils.getCurrentUsername());

        List<ZonaResponse> zonas = zonaService.listarPorCiudad(codCiudad);

        return Response.ok(ApiResponse.success("Operación exitosa", zonas)).build();
    }

    /**
     * GET /api/zonas/buscar?nombre=xxx - Buscar por nombre
     */
    @GET
    @Path("/buscar")
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/zonas/buscar?nombre={} - Usuario: {}", nombre, securityUtils.getCurrentUsername());

        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'nombre' es obligatorio"))
                    .build();
        }

        List<ZonaResponse> zonas = zonaService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success("Operación exitosa", zonas)).build();
    }

    /**
     * POST /api/zonas - Crear nueva zona
     */
    @POST
    @RolesAllowed("admin")
    public Response crearZona(@Valid ZonaRequest request) {
        log.info("POST /api/zonas - Usuario: {}", securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        int codZona = zonaService.crearZona(request, audUsuario);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Zona creada exitosamente", codZona))
                .build();
    }

    /**
     * PUT /api/zonas/{codZona} - Actualizar zona
     */
    @PUT
    @Path("/{codZona}")
    @RolesAllowed("admin")
    public Response actualizarZona(
            @PathParam("codZona") int codZona,
            @Valid ZonaRequest request) {
        log.info("PUT /api/zonas/{} - Usuario: {}", codZona, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        zonaService.actualizarZona(codZona, request, audUsuario);

        return Response.ok(ApiResponse.success("Zona actualizada exitosamente", null)).build();
    }

    /**
     * DELETE /api/zonas/{codZona} - Eliminar zona
     */
    @DELETE
    @Path("/{codZona}")
    @RolesAllowed("admin")
    public Response eliminarZona(@PathParam("codZona") int codZona) {
        log.info("DELETE /api/zonas/{} - Usuario: {}", codZona, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        zonaService.eliminarZona(codZona, audUsuario);

        return Response.ok(ApiResponse.success("Zona eliminada exitosamente", null)).build();
    }
}