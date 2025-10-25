package com.yahveh.resource;

import com.yahveh.dto.request.ClienteRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.ClienteResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.ClienteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Path("/api/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * GET /api/clientes - Listar todos los clientes
     */
    @GET
    public Response listarTodos() {
        log.info("GET /api/clientes - Usuario: {}", securityUtils.getCurrentUserLogin());

        List<ClienteResponse> clientes = clienteService.listarTodos();

        return Response.ok(ApiResponse.success("Operación exitosa", clientes)).build();
    }

    /**
     * GET /api/clientes/{codCliente} - Buscar cliente por ID
     */
    @GET
    @Path("/{codCliente}")
    public Response buscarPorId(@PathParam("codCliente") int codCliente) {
        log.info("GET /api/clientes/{} - Usuario: {}", codCliente, securityUtils.getCurrentUsername());

        ClienteResponse cliente = clienteService.buscarPorId(codCliente);

        return Response.ok(ApiResponse.success("Cliente encontrado", cliente)).build();
    }

    /**
     * GET /api/clientes/zona/{codZona} - Listar clientes por zona
     */
    @GET
    @Path("/zona/{codZona}")
    public Response listarPorZona(@PathParam("codZona") int codZona) {
        log.info("GET /api/clientes/zona/{} - Usuario: {}", codZona, securityUtils.getCurrentUsername());

        List<ClienteResponse> clientes = clienteService.listarPorZona(codZona);

        return Response.ok(ApiResponse.success("Operación exitosa", clientes)).build();
    }

    /**
     * GET /api/clientes/buscar/nit?nit=xxx - Buscar por NIT
     */
    @GET
    @Path("/buscar/nit")
    public Response buscarPorNit(@QueryParam("nit") String nit) {
        log.info("GET /api/clientes/buscar/nit?nit={} - Usuario: {}", nit, securityUtils.getCurrentUsername());

        if (nit == null || nit.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'nit' es obligatorio"))
                    .build();
        }

        List<ClienteResponse> clientes = clienteService.buscarPorNit(nit);

        return Response.ok(ApiResponse.success("Operación exitosa", clientes)).build();
    }

    /**
     * GET /api/clientes/buscar/nombre?nombre=xxx - Buscar por nombre
     */
    @GET
    @Path("/buscar/nombre")
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/clientes/buscar/nombre?nombre={} - Usuario: {}", nombre, securityUtils.getCurrentUsername());

        if (nombre == null || nombre.trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("El parámetro 'nombre' es obligatorio"))
                    .build();
        }

        List<ClienteResponse> clientes = clienteService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success("Operación exitosa", clientes)).build();
    }

    /**
     * POST /api/clientes - Crear nuevo cliente
     */
    @POST
    @RolesAllowed("admin")
    public Response crearCliente(@Valid ClienteRequest request) {
        log.info("POST /api/clientes - Usuario: {}", securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        int codCliente = clienteService.crearCliente(request, audUsuario);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Cliente creado exitosamente", codCliente))
                .build();
    }

    /**
     * PUT /api/clientes/{codCliente} - Actualizar cliente
     */
    @PUT
    @Path("/{codCliente}")
    @RolesAllowed("admin")
    public Response actualizarCliente(
            @PathParam("codCliente") int codCliente,
            @Valid ClienteRequest request) {
        log.info("PUT /api/clientes/{} - Usuario: {}", codCliente, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        clienteService.actualizarCliente(codCliente, request, audUsuario);

        return Response.ok(ApiResponse.success("Cliente actualizado exitosamente", null)).build();
    }

    /**
     * DELETE /api/clientes/{codCliente} - Eliminar cliente
     */
    @DELETE
    @Path("/{codCliente}")
    @RolesAllowed("admin")
    public Response eliminarCliente(@PathParam("codCliente") int codCliente) {
        log.info("DELETE /api/clientes/{} - Usuario: {}", codCliente, securityUtils.getCurrentUsername());

        int audUsuario = securityUtils.getCurrentUserId();
        clienteService.eliminarCliente(codCliente, audUsuario);

        return Response.ok(ApiResponse.success("Cliente eliminado exitosamente", null)).build();
    }
}