package com.yahveh.resource;

import com.yahveh.dto.request.CiudadRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.CiudadResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.CiudadService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/api/ciudades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
public class CiudadResource {

    @Inject
    CiudadService ciudadService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/ciudades - Listar todas las ciudades
     */
    @GET
    public Response listarTodas() {
        log.info("GET /api/ciudades - Usuario: {}", securityUtils.getCurrentUsername());

        List<CiudadResponse> ciudades = ciudadService.listarTodas();

        return Response.ok(ApiResponse.success("Operación exitosa", ciudades)).build();
    }

    /**
     * GET /api/ciudades/{codCiudad} - Buscar ciudad por ID
     */
    @GET
    @Path("/{codCiudad}")
    public Response buscarPorId(@PathParam("codCiudad") int codCiudad) {
        log.info("GET /api/ciudades/{} - Usuario: {}", codCiudad, securityUtils.getCurrentUsername());

        CiudadResponse ciudad = ciudadService.buscarPorId(codCiudad);

        return Response.ok(ApiResponse.success("Ciudad encontrada", ciudad)).build();
    }

    /**
     * GET /api/ciudades/pais/{codPais} - Listar ciudades por país
     */
    @GET
    @Path("/pais/{codPais}")
    public Response listarPorPais(@PathParam("codPais") int codPais) {
        log.info("GET /api/ciudades/pais/{} - Usuario: {}", codPais, securityUtils.getCurrentUsername());

        List<CiudadResponse> ciudades = ciudadService.listarPorPais(codPais);

        return Response.ok(ApiResponse.success("Operación exitosa", ciudades)).build();
    }

    /**
     * GET /api/ciudades/buscar?nombre=xxx - Buscar por nombre
     */
    @GET
    @Path("/buscar")
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/ciudades/buscar?nombre={} - Usuario: {}", nombre, securityUtils.getCurrentUsername());

        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'nombre' es obligatorio"))
                    .build();
        }

        List<CiudadResponse> ciudades = ciudadService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success("Operación exitosa", ciudades)).build();
    }

    /**
     * POST /api/ciudades - Crear nueva ciudad
     */
    @POST
    @RolesAllowed("admin")
    public Response crearCiudad(@Valid CiudadRequest request) {
        log.info("POST /api/ciudades - Usuario: {}", securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        int codCiudad = ciudadService.crearCiudad(request, audUsuario);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Ciudad creada exitosamente", codCiudad))
                .build();
    }

    /**
     * PUT /api/ciudades/{codCiudad} - Actualizar ciudad
     */
    @PUT
    @Path("/{codCiudad}")
    @RolesAllowed("admin")
    public Response actualizarCiudad(
            @PathParam("codCiudad") int codCiudad,
            @Valid CiudadRequest request) {
        log.info("PUT /api/ciudades/{} - Usuario: {}", codCiudad, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        ciudadService.actualizarCiudad(codCiudad, request, audUsuario);

        return Response.ok(ApiResponse.success("Ciudad actualizada exitosamente", null)).build();
    }

    /**
     * DELETE /api/ciudades/{codCiudad} - Eliminar ciudad
     */
    @DELETE
    @Path("/{codCiudad}")
    @RolesAllowed("admin")
    public Response eliminarCiudad(@PathParam("codCiudad") int codCiudad) {
        log.info("DELETE /api/ciudades/{} - Usuario: {}", codCiudad, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        ciudadService.eliminarCiudad(codCiudad, audUsuario);

        return Response.ok(ApiResponse.success("Ciudad eliminada exitosamente", null)).build();
    }
}