package com.yahveh.resource;

import com.yahveh.dto.request.InventarioRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.InventarioResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.InventarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

@Path("/api/inventario")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Slf4j
public class InventarioResource {

    @Inject
    InventarioService inventarioService;

    @Inject
    SecurityUtils securityUtils;

    @GET
    public Response listar() {
        log.info("GET /api/inventario - Usuario: {}", securityUtils.getCurrentUsername());

        List<InventarioResponse> movimientos = inventarioService.listar();

        return Response.ok(ApiResponse.success("Operación exitosa", movimientos)).build();
    }

    @GET
    @Path("/{codInventario}")
    public Response buscarPorCodigo(@PathParam("codInventario") int codInventario) {
        log.info("GET /api/inventario/{} - Usuario: {}", codInventario, securityUtils.getCurrentUsername());

        InventarioResponse movimiento = inventarioService.buscarPorCodigo(codInventario);

        return Response.ok(ApiResponse.success("Operación exitosa", movimiento)).build();
    }

    @GET
    @Path("/articulo/{codArticulo}")
    public Response listarPorArticulo(@PathParam("codArticulo") String codArticulo) {
        log.info("GET /api/inventario/articulo/{} - Usuario: {}", codArticulo, securityUtils.getCurrentUsername());

        List<InventarioResponse> movimientos = inventarioService.listarPorArticulo(codArticulo);

        return Response.ok(ApiResponse.success("Operación exitosa", movimientos)).build();
    }

    @GET
    @Path("/tipo/{tipoMovimiento}")
    public Response listarPorTipo(@PathParam("tipoMovimiento") String tipoMovimiento) {
        log.info("GET /api/inventario/tipo/{} - Usuario: {}", tipoMovimiento, securityUtils.getCurrentUsername());

        List<InventarioResponse> movimientos = inventarioService.listarPorTipo(tipoMovimiento);

        return Response.ok(ApiResponse.success("Operación exitosa", movimientos)).build();
    }

    @GET
    @Path("/fechas")
    public Response listarPorFechas(
            @QueryParam("desde") String fechaDesde,
            @QueryParam("hasta") String fechaHasta) {
        log.info("GET /api/inventario/fechas?desde={}&hasta={} - Usuario: {}",
                fechaDesde, fechaHasta, securityUtils.getCurrentUsername());

        LocalDate desde = fechaDesde != null ? LocalDate.parse(fechaDesde) : null;
        LocalDate hasta = fechaHasta != null ? LocalDate.parse(fechaHasta) : null;

        List<InventarioResponse> movimientos = inventarioService.listarPorFechas(desde, hasta);

        return Response.ok(ApiResponse.success("Operación exitosa", movimientos)).build();
    }

    @POST
    public Response crear(InventarioRequest request) {
        log.info("POST /api/inventario - Usuario: {} - Artículo: {}",
                securityUtils.getCurrentUsername(), request.getCodArticulo());

        InventarioResponse movimiento = inventarioService.crear(request);

        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Movimiento creado exitosamente", movimiento))
                .build();
    }

    @PUT
    @Path("/{codInventario}")
    public Response modificar(@PathParam("codInventario") int codInventario, InventarioRequest request) {
        log.info("PUT /api/inventario/{} - Usuario: {}", codInventario, securityUtils.getCurrentUsername());

        InventarioResponse movimiento = inventarioService.modificar(codInventario, request);

        return Response.ok(ApiResponse.success("Observación modificada exitosamente", movimiento)).build();
    }

    @DELETE
    @Path("/{codInventario}")
    public Response eliminar(@PathParam("codInventario") int codInventario) {
        log.info("DELETE /api/inventario/{} - Usuario: {}", codInventario, securityUtils.getCurrentUsername());

        inventarioService.eliminar(codInventario);

        return Response.ok(ApiResponse.success("Movimiento reversado exitosamente", null)).build();
    }
}