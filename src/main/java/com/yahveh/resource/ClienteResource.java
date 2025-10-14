package com.yahveh.resource;

import com.yahveh.dto.request.CrearClienteRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.ClienteResponse;
import com.yahveh.service.ClienteService;
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
@Path("/api/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService clienteService;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"ADMIN", "VENDEDOR"})
    public Response listarClientes() {
        log.info("Listando clientes - Usuario: {}", jwt.getName());

        List<ClienteResponse> clientes = clienteService.listarTodos();
        return Response.ok(ApiResponse.success(clientes)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "VENDEDOR"})
    public Response buscarCliente(@PathParam("id") Long id) {
        log.info("Buscando cliente {} - Usuario: {}", id, jwt.getName());

        ClienteResponse cliente = clienteService.buscarPorId(id);
        return Response.ok(ApiResponse.success(cliente)).build();
    }

    @POST
    @RolesAllowed({"ADMIN"})
    public Response crearCliente(@Valid CrearClienteRequest request) {
        Long codUsuario = jwt.getClaim("codUsuario");
        log.info("Creando cliente - Usuario: {}", jwt.getName());

        Long codCliente = clienteService.crearCliente(request, codUsuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Cliente creado exitosamente", codCliente))
                .build();
    }
}