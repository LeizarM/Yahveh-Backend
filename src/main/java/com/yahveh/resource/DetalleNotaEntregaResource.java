package com.yahveh.resource;

import com.yahveh.dto.request.DetalleNotaEntregaRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.DetalleNotaEntregaResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.DetalleNotaEntregaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/api/detalles-nota-entrega")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
@Slf4j
public class DetalleNotaEntregaResource {

    @Inject
    DetalleNotaEntregaService detalleService;

    @Inject
    SecurityUtils securityUtils;

    @GET
    @Path("/nota/{codNotaEntrega}")
    public Response listarPorNotaEntrega(@PathParam("codNotaEntrega") int codNotaEntrega) {
        log.info("GET /api/detalles-nota-entrega/nota/{} - Usuario: {}",
                codNotaEntrega, securityUtils.getCurrentUsername());

        List<DetalleNotaEntregaResponse> detalles = detalleService.listarPorNotaEntrega(codNotaEntrega);

        return Response.ok(ApiResponse.success("Operación exitosa", detalles)).build();
    }

    @GET
    @Path("/{codDetalle}")
    public Response buscarPorCodigo(@PathParam("codDetalle") int codDetalle) {
        log.info("GET /api/detalles-nota-entrega/{} - Usuario: {}",
                codDetalle, securityUtils.getCurrentUsername());

        DetalleNotaEntregaResponse detalle = detalleService.buscarPorCodigo(codDetalle);

        return Response.ok(ApiResponse.success("Operación exitosa", detalle)).build();
    }

    @POST
    @Path("/nota/{codNotaEntrega}")
    public Response crear(@PathParam("codNotaEntrega") int codNotaEntrega, DetalleNotaEntregaRequest request) {
        log.info("POST /api/detalles-nota-entrega/nota/{} - Usuario: {} - Artículo: {}",
                codNotaEntrega, securityUtils.getCurrentUsername(), request.getCodArticulo());

        DetalleNotaEntregaResponse detalle = detalleService.crear(codNotaEntrega, request);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Detalle agregado exitosamente", detalle))
                .build();
    }

    @PUT
    @Path("/{codDetalle}")
    public Response actualizar(@PathParam("codDetalle") int codDetalle, DetalleNotaEntregaRequest request) {
        log.info("PUT /api/detalles-nota-entrega/{} - Usuario: {}",
                codDetalle, securityUtils.getCurrentUsername());

        DetalleNotaEntregaResponse detalle = detalleService.actualizar(codDetalle, request);

        return Response.ok(ApiResponse.success("Detalle actualizado exitosamente", detalle)).build();
    }

    @DELETE
    @Path("/{codDetalle}")
    public Response eliminar(@PathParam("codDetalle") int codDetalle) {
        log.info("DELETE /api/detalles-nota-entrega/{} - Usuario: {}",
                codDetalle, securityUtils.getCurrentUsername());

        detalleService.eliminar(codDetalle);

        return Response.ok(ApiResponse.success("Detalle eliminado exitosamente", null)).build();
    }
}