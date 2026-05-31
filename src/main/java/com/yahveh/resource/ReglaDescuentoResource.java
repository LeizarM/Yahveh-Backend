package com.yahveh.resource;

import com.yahveh.dto.request.ReglaDescuentoRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.ReglaDescuentoResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.ReglaDescuentoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Path("/api/regla-descuento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"admin", "lim"})
@com.yahveh.security.RequiereVista({"regla-descuento", "items"})
@Slf4j
public class ReglaDescuentoResource {

    @Inject
    ReglaDescuentoService reglaDescuentoService;

    @Inject
    SecurityUtils securityUtils;

    @GET
    @RolesAllowed({"admin"})
    public Response listarTodas() {
        log.info("GET /api/regla-descuento - Usuario: {}", securityUtils.getCurrentUsername());
        List<ReglaDescuentoResponse> reglas = reglaDescuentoService.listarTodas();
        return Response.ok(ApiResponse.success("Operación exitosa", reglas)).build();
    }

    @GET
    @Path("/articulo/{codArticulo}")
    public Response buscarPorArticulo(@PathParam("codArticulo") String codArticulo) {
        log.info("GET /api/regla-descuento/articulo/{} - Usuario: {}", codArticulo, securityUtils.getCurrentUsername());
        try {
            ReglaDescuentoResponse regla = reglaDescuentoService.buscarPorArticulo(codArticulo);
            return Response.ok(ApiResponse.success("Operación exitosa", regla)).build();
        } catch (NotFoundException e) {
            return Response.ok(ApiResponse.success("Sin regla de descuento", null)).build();
        }
    }

    @POST
    @RolesAllowed({"admin"})
    public Response crear(ReglaDescuentoRequest request) {
        log.info("POST /api/regla-descuento - Artículo: {}", request.getCodArticulo());
        ReglaDescuentoResponse regla = reglaDescuentoService.crear(request);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Regla creada exitosamente", regla))
                .build();
    }

    @PUT
    @Path("/{codReglaDescuento}")
    @RolesAllowed({"admin"})
    public Response actualizar(@PathParam("codReglaDescuento") long codReglaDescuento,
                               ReglaDescuentoRequest request) {
        log.info("PUT /api/regla-descuento/{}", codReglaDescuento);
        ReglaDescuentoResponse regla = reglaDescuentoService.actualizar(codReglaDescuento, request);
        return Response.ok(ApiResponse.success("Regla actualizada exitosamente", regla)).build();
    }

    @DELETE
    @Path("/{codReglaDescuento}")
    @RolesAllowed({"admin"})
    public Response eliminar(@PathParam("codReglaDescuento") long codReglaDescuento) {
        log.info("DELETE /api/regla-descuento/{}", codReglaDescuento);
        reglaDescuentoService.eliminar(codReglaDescuento);
        return Response.ok(ApiResponse.success("Regla eliminada exitosamente", null)).build();
    }
}
