package com.yahveh.resource;

import com.yahveh.dto.request.NotaEntregaRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.NotaEntregaResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.NotaEntregaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Path("/api/notas-entrega")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
@Slf4j
public class NotaEntregaResource {

    @Inject
    NotaEntregaService notaEntregaService;

    @Inject
    SecurityUtils securityUtils;

    /**
     * Listar solo notas válidas
     */
    @GET
    public Response listar() {
        log.info("GET /api/notas-entrega - Usuario: {}", securityUtils.getCurrentUsername());

        List<NotaEntregaResponse> notas = notaEntregaService.listar();

        return Response.ok(ApiResponse.success("Operación exitosa", notas)).build();
    }

    /**
     * ⭐ Listar todas las notas (válidas y anuladas)
     */
    @GET
    @Path("/todas")
    public Response listarTodas() {
        log.info("GET /api/notas-entrega/todas - Usuario: {}", securityUtils.getCurrentUsername());

        List<NotaEntregaResponse> notas = notaEntregaService.listarTodasConAnuladas();

        return Response.ok(ApiResponse.success("Operación exitosa", notas)).build();
    }

    /**
     * ⭐ Listar solo notas anuladas
     */
    @GET
    @Path("/anuladas")
    public Response listarAnuladas() {
        log.info("GET /api/notas-entrega/anuladas - Usuario: {}", securityUtils.getCurrentUsername());

        List<NotaEntregaResponse> notas = notaEntregaService.listarAnuladas();

        return Response.ok(ApiResponse.success("Operación exitosa", notas)).build();
    }

    @GET
    @Path("/{codNotaEntrega}")
    public Response buscarPorCodigo(@PathParam("codNotaEntrega") int codNotaEntrega) {
        log.info("GET /api/notas-entrega/{} - Usuario: {}", codNotaEntrega, securityUtils.getCurrentUsername());

        NotaEntregaResponse nota = notaEntregaService.buscarPorCodigo(codNotaEntrega);

        return Response.ok(ApiResponse.success("Operación exitosa", nota)).build();
    }

    @GET
    @Path("/cliente/{codCliente}")
    public Response listarPorCliente(@PathParam("codCliente") int codCliente) {
        log.info("GET /api/notas-entrega/cliente/{} - Usuario: {}", codCliente, securityUtils.getCurrentUsername());

        List<NotaEntregaResponse> notas = notaEntregaService.listarPorCliente(codCliente);

        return Response.ok(ApiResponse.success("Operación exitosa", notas)).build();
    }

    @GET
    @Path("/fechas")
    public Response listarPorFechas(
            @QueryParam("desde") String fechaDesde,
            @QueryParam("hasta") String fechaHasta) {
        log.info("GET /api/notas-entrega/fechas?desde={}&hasta={} - Usuario: {}",
                fechaDesde, fechaHasta, securityUtils.getCurrentUsername());

        LocalDate desde = fechaDesde != null ? LocalDate.parse(fechaDesde) : null;
        LocalDate hasta = fechaHasta != null ? LocalDate.parse(fechaHasta) : null;

        List<NotaEntregaResponse> notas = notaEntregaService.listarPorFechas(desde, hasta);

        return Response.ok(ApiResponse.success("Operación exitosa", notas)).build();
    }

    @POST
    public Response crear(NotaEntregaRequest request) {
        log.info("POST /api/notas-entrega - Usuario: {} - Cliente: {}",
                securityUtils.getCurrentUsername(), request.getCodCliente());

        NotaEntregaResponse nota = notaEntregaService.crear(request);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Nota de entrega creada exitosamente", nota))
                .build();
    }

    @PUT
    @Path("/{codNotaEntrega}")
    public Response actualizar(@PathParam("codNotaEntrega") int codNotaEntrega, NotaEntregaRequest request) {
        log.info("PUT /api/notas-entrega/{} - Usuario: {}", codNotaEntrega, securityUtils.getCurrentUsername());

        NotaEntregaResponse nota = notaEntregaService.actualizar(codNotaEntrega, request);

        return Response.ok(ApiResponse.success("Nota de entrega actualizada exitosamente", nota)).build();
    }

    /**
     * ⭐ ANULAR nota de entrega (devuelve stock automáticamente)
     */
    @PUT
    @Path("/{codNotaEntrega}/anular")
    public Response anular(@PathParam("codNotaEntrega") int codNotaEntrega) {
        log.info("PUT /api/notas-entrega/{}/anular - Usuario: {}", codNotaEntrega, securityUtils.getCurrentUsername());

        NotaEntregaResponse nota = notaEntregaService.anular(codNotaEntrega);

        return Response.ok(ApiResponse.success("Nota de entrega anulada exitosamente. Stock devuelto al inventario", nota)).build();
    }

    @DELETE
    @Path("/{codNotaEntrega}")
    public Response eliminar(@PathParam("codNotaEntrega") int codNotaEntrega) {
        log.info("DELETE /api/notas-entrega/{} - Usuario: {}", codNotaEntrega, securityUtils.getCurrentUsername());

        notaEntregaService.eliminar(codNotaEntrega);

        return Response.ok(ApiResponse.success("Nota de entrega eliminada exitosamente", null)).build();
    }
}