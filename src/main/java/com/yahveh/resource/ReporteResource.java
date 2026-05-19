package com.yahveh.resource;

import com.yahveh.service.NotaEntregaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Path("/api/reportes")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ReporteResource {

    @Inject
    NotaEntregaService notaEntregaService;

    @GET
    @Path("/nota-entrega/{codNotaEntrega}")
    @Produces("application/pdf")
    @RolesAllowed({"admin", "lim"})
    public Response generarNotaEntregaPDF(@PathParam("codNotaEntrega") long codNotaEntrega) {
        log.info("Generando PDF de nota de entrega: {}", codNotaEntrega);
        byte[] pdfBytes = notaEntregaService.generarPDF(codNotaEntrega);
        return Response.ok(pdfBytes)
                .header("Content-Disposition", "inline; filename=\"nota_entrega_" + codNotaEntrega + ".pdf\"")
                .build();
    }

    @GET
    @Path("/vendedores/pdf/{fechaDesde}/{fechaHasta}")
    @Produces("application/pdf")
    @RolesAllowed("admin")
    public Response generarReporteVendedoresPDF(
            @PathParam("fechaDesde") String fechaDesde,
            @PathParam("fechaHasta") String fechaHasta) {
        log.info("Generando PDF reporte vendedores: {} - {}", fechaDesde, fechaHasta);
        byte[] pdfBytes = notaEntregaService.generarReporteVendedores(
                LocalDate.parse(fechaDesde), LocalDate.parse(fechaHasta));
        return Response.ok(pdfBytes)
                .header("Content-Disposition", "attachment; filename=\"reporte_vendedores.pdf\"")
                .build();
    }

    @GET
    @Path("/inventario/pdf/{fechaDesde}/{fechaHasta}")
    @Produces("application/pdf")
    @RolesAllowed({"admin", "lim"})
    public Response generarReporteInventarioPDF(
            @PathParam("fechaDesde") String fechaDesde,
            @PathParam("fechaHasta") String fechaHasta) {
        log.info("Generando PDF reporte inventario: {} - {}", fechaDesde, fechaHasta);
        byte[] pdfBytes = notaEntregaService.generarReporteInventario(
                LocalDate.parse(fechaDesde), LocalDate.parse(fechaHasta));
        return Response.ok(pdfBytes)
                .header("Content-Disposition", "attachment; filename=\"reporte_inventario.pdf\"")
                .build();
    }
}