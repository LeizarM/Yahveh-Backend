package com.yahveh.resource;


import com.yahveh.dto.request.TelefonoClienteRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.TelefonoClienteResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.TelefonoClienteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/api/telefonos-cliente")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class TelefonoClienteResource {

    @Inject
    TelefonoClienteService telefonoClienteService;

    @Inject
    SecurityUtils securityUtils;

    @GET
    @RolesAllowed({"admin", "user"})
    public Response listar() {
        log.info("GET /api/telefonos-cliente - Usuario: {}", securityUtils.getCurrentUsername());

        List<TelefonoClienteResponse> telefonos = telefonoClienteService.listar();

        return Response.ok(ApiResponse.success("Operación exitosa", telefonos)).build();
    }

    @GET
    @Path("/{codTlfCliente}")
    @RolesAllowed({"admin", "user"})
    public Response buscarPorCodigo(@PathParam("codTlfCliente") long codTlfCliente) {
        log.info("GET /api/telefonos-cliente/{} - Usuario: {}",
                codTlfCliente, securityUtils.getCurrentUsername());

        TelefonoClienteResponse telefono = telefonoClienteService.buscarPorCodigo(codTlfCliente);

        return Response.ok(ApiResponse.success("Operación exitosa", telefono)).build();
    }

    @GET
    @Path("/cliente/{codCliente}")
    @RolesAllowed({"admin", "user"})
    public Response listarPorCliente(@PathParam("codCliente") long codCliente) {
        log.info("GET /api/telefonos-cliente/cliente/{} - Usuario: {}",
                codCliente, securityUtils.getCurrentUsername());

        List<TelefonoClienteResponse> telefonos = telefonoClienteService.listarPorCliente(codCliente);

        return Response.ok(ApiResponse.success("Operación exitosa", telefonos)).build();
    }

    @POST
    @RolesAllowed({"admin", "user"})
    public Response crear(TelefonoClienteRequest request) {
        log.info("POST /api/telefonos-cliente - Usuario: {} - Cliente: {}",
                securityUtils.getCurrentUsername(), request.getCodCliente());

        TelefonoClienteResponse telefono = telefonoClienteService.crear(request);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Teléfono creado exitosamente", telefono))
                .build();
    }

    @PUT
    @Path("/{codTlfCliente}")
    @RolesAllowed({"admin", "user"})
    public Response actualizar(
            @PathParam("codTlfCliente") long codTlfCliente,
            TelefonoClienteRequest request) {
        log.info("PUT /api/telefonos-cliente/{} - Usuario: {}",
                codTlfCliente, securityUtils.getCurrentUsername());

        TelefonoClienteResponse telefono = telefonoClienteService.actualizar(codTlfCliente, request);

        return Response.ok(ApiResponse.success("Teléfono actualizado exitosamente", telefono)).build();
    }

    @DELETE
    @Path("/{codTlfCliente}")
    @RolesAllowed({"admin", "user"})
    public Response eliminar(@PathParam("codTlfCliente") long codTlfCliente) {
        log.info("DELETE /api/telefonos-cliente/{} - Usuario: {}",
                codTlfCliente, securityUtils.getCurrentUsername());

        telefonoClienteService.eliminar(codTlfCliente);

        return Response.ok(ApiResponse.success("Teléfono eliminado exitosamente", null)).build();
    }
}