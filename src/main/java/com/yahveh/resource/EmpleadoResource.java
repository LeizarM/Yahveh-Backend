package com.yahveh.resource;


import com.yahveh.dto.request.EmpleadoRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.EmpleadoResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.EmpleadoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/api/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class EmpleadoResource {

    @Inject
    EmpleadoService empleadoService;

    @Inject
    SecurityUtils securityUtils;

    @GET
    @RolesAllowed({"admin"})
    public Response listar() {
        log.info("GET /api/empleados - Usuario: {}", securityUtils.getCurrentUsername());

        List<EmpleadoResponse> empleados = empleadoService.listar();

        return Response.ok(ApiResponse.success("Operación exitosa", empleados)).build();
    }

    @GET
    @Path("/{codEmpleado}")
    @RolesAllowed({"admin"})
    public Response buscarPorCodigo(@PathParam("codEmpleado") long codEmpleado) {
        log.info("GET /api/empleados/{} - Usuario: {}", codEmpleado, securityUtils.getCurrentUsername());

        EmpleadoResponse empleado = empleadoService.buscarPorCodigo(codEmpleado);

        return Response.ok(ApiResponse.success("Operación exitosa", empleado)).build();
    }

    @GET
    @Path("/persona/{codPersona}")
    @RolesAllowed({"admin"})
    public Response buscarPorPersona(@PathParam("codPersona") long codPersona) {
        log.info("GET /api/empleados/persona/{} - Usuario: {}",
                codPersona, securityUtils.getCurrentUsername());

        EmpleadoResponse empleado = empleadoService.buscarPorPersona(codPersona);

        return Response.ok(ApiResponse.success("Operación exitosa", empleado)).build();
    }

    @GET
    @Path("/buscar")
    @RolesAllowed({"admin"})
    public Response buscarPorNombre(@QueryParam("nombre") String nombre) {
        log.info("GET /api/empleados/buscar?nombre={} - Usuario: {}",
                nombre, securityUtils.getCurrentUsername());

        List<EmpleadoResponse> empleados = empleadoService.buscarPorNombre(nombre);

        return Response.ok(ApiResponse.success("Operación exitosa", empleados)).build();
    }

    @POST
    @RolesAllowed({"admin"})
    public Response crear(EmpleadoRequest request) {
        log.info("POST /api/empleados - Usuario: {} - Persona: {}",
                securityUtils.getCurrentUsername(), request.getCodPersona());

        EmpleadoResponse empleado = empleadoService.crear(request);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Empleado creado exitosamente", empleado))
                .build();
    }

    @PUT
    @Path("/{codEmpleado}")
    @RolesAllowed({"admin"})
    public Response actualizar(@PathParam("codEmpleado") long codEmpleado, EmpleadoRequest request) {
        log.info("PUT /api/empleados/{} - Usuario: {}", codEmpleado, securityUtils.getCurrentUsername());

        EmpleadoResponse empleado = empleadoService.actualizar(codEmpleado, request);

        return Response.ok(ApiResponse.success("Empleado actualizado exitosamente", empleado)).build();
    }

    @DELETE
    @Path("/{codEmpleado}")
    @RolesAllowed({"admin"})
    public Response eliminar(@PathParam("codEmpleado") long codEmpleado) {
        log.info("DELETE /api/empleados/{} - Usuario: {}", codEmpleado, securityUtils.getCurrentUsername());

        empleadoService.eliminar(codEmpleado);

        return Response.ok(ApiResponse.success("Empleado eliminado exitosamente", null)).build();
    }
}