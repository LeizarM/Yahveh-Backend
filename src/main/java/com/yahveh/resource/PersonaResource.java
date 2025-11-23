package com.yahveh.resource;


import com.yahveh.dto.request.PersonaRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.PersonaResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.PersonaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/api/personas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class PersonaResource {

    @Inject
    PersonaService personaService;

    @Inject
    SecurityUtils securityUtils;

    @GET
    @RolesAllowed({"admin"})
    public Response listar() {
        log.info("GET /api/personas - Usuario: {}", securityUtils.getCurrentUsername());

        List<PersonaResponse> personas = personaService.listar();

        return Response.ok(ApiResponse.success("Operación exitosa", personas)).build();
    }

    @GET
    @Path("/{codPersona}")
    @RolesAllowed({"admin"})
    public Response buscarPorCodigo(@PathParam("codPersona") long codPersona) {
        log.info("GET /api/personas/{} - Usuario: {}", codPersona, securityUtils.getCurrentUsername());

        PersonaResponse persona = personaService.buscarPorCodigo(codPersona);

        return Response.ok(ApiResponse.success("Operación exitosa", persona)).build();
    }

    @GET
    @Path("/ci/{ciNumero}/{ciExpedido}")
    @RolesAllowed({"admin"})
    public Response buscarPorCI(
            @PathParam("ciNumero") String ciNumero,
            @PathParam("ciExpedido") String ciExpedido) {
        log.info("GET /api/personas/ci/{}/{} - Usuario: {}",
                ciNumero, ciExpedido, securityUtils.getCurrentUsername());

        PersonaResponse persona = personaService.buscarPorCI(ciNumero, ciExpedido);

        return Response.ok(ApiResponse.success("Operación exitosa", persona)).build();
    }

    @GET
    @Path("/buscar")
    @RolesAllowed({"admin"})
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/personas/buscar?nombre={} - Usuario: {}",
                nombre, securityUtils.getCurrentUsername());

        List<PersonaResponse> personas = personaService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success("Operación exitosa", personas)).build();
    }

    @POST
    @RolesAllowed({"admin"})
    public Response crear(PersonaRequest request) {
        log.info("POST /api/personas - Usuario: {} - Persona: {} {}",
                securityUtils.getCurrentUsername(), request.getNombres(), request.getApPaterno());

        System.out.println(request.toString());
        PersonaResponse persona = personaService.crear(request);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Persona creada exitosamente", persona))
                .build();
    }

    @PUT
    @Path("/{codPersona}")
    @RolesAllowed({"admin"})
    public Response actualizar(@PathParam("codPersona") long codPersona, PersonaRequest request) {
        log.info("PUT /api/personas/{} - Usuario: {}", codPersona, securityUtils.getCurrentUsername());

        PersonaResponse persona = personaService.actualizar(codPersona, request);

        return Response.ok(ApiResponse.success("Persona actualizada exitosamente", persona)).build();
    }

    @DELETE
    @Path("/{codPersona}")
    @RolesAllowed({"admin"})
    public Response eliminar(@PathParam("codPersona") long codPersona) {
        log.info("DELETE /api/personas/{} - Usuario: {}", codPersona, securityUtils.getCurrentUsername());

        personaService.eliminar(codPersona);

        return Response.ok(ApiResponse.success("Persona eliminada exitosamente", null)).build();
    }
}