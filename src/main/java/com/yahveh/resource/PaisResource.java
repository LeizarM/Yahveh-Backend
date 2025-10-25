package com.yahveh.resource;

import com.yahveh.dto.request.PaisRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.PaisResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.PaisService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/api/paises")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
public class PaisResource {

    @Inject
    PaisService paisService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/paises - Listar todos los países
     */
    @GET
    public Response listarTodos() {
        log.info("GET /api/paises - Usuario: {}", securityUtils.getCurrentUsername());

        List<PaisResponse> paises = paisService.listarTodos();

        return Response.ok(ApiResponse.success("Operación exitosa", paises)).build();
    }

    /**
     * GET /api/paises/{codPais} - Buscar país por ID
     */
    @GET
    @Path("/{codPais}")
    public Response buscarPorId(@PathParam("codPais") int codPais) {
        log.info("GET /api/paises/{} - Usuario: {}", codPais, securityUtils.getCurrentUsername());

        PaisResponse pais = paisService.buscarPorId(codPais);

        return Response.ok(ApiResponse.success("País encontrado", pais)).build();
    }

    /**
     * GET /api/paises/buscar?nombre=xxx - Buscar por nombre
     */
    @GET
    @Path("/buscar")
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/paises/buscar?nombre={} - Usuario: {}", nombre, securityUtils.getCurrentUsername());

        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'nombre' es obligatorio"))
                    .build();
        }

        List<PaisResponse> paises = paisService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success("Operación exitosa", paises)).build();
    }

    /**
     * POST /api/paises - Crear nuevo país
     */
    @POST
    @RolesAllowed("admin")
    public Response crearPais(@Valid PaisRequest request) {
        log.info("POST /api/paises - Usuario: {}", securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        int codPais = paisService.crearPais(request, audUsuario);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("País creado exitosamente", codPais))
                .build();
    }

    /**
     * PUT /api/paises/{codPais} - Actualizar país
     */
    @PUT
    @Path("/{codPais}")
    @RolesAllowed("admin")
    public Response actualizarPais(
            @PathParam("codPais") int codPais,
            @Valid PaisRequest request) {
        log.info("PUT /api/paises/{} - Usuario: {}", codPais, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        paisService.actualizarPais(codPais, request, audUsuario);

        return Response.ok(ApiResponse.success("País actualizado exitosamente", null)).build();
    }

    /**
     * DELETE /api/paises/{codPais} - Eliminar país
     */
    @DELETE
    @Path("/{codPais}")
    @RolesAllowed("admin")
    public Response eliminarPais(@PathParam("codPais") int codPais) {
        log.info("DELETE /api/paises/{} - Usuario: {}", codPais, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        paisService.eliminarPais(codPais, audUsuario);

        return Response.ok(ApiResponse.success("País eliminado exitosamente", null)).build();
    }
}