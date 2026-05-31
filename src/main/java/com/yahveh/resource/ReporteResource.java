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
@com.yahveh.security.RequiereVista({"reportes"})
@Slf4j
public class ReporteResource {

    @Inject
    NotaEntregaService notaEntregaService;

    @GET
    @Path("/nota-entrega/{codNotaEntrega}")
    @Produces("application/pdf")
    @RolesAllowed({"admin", "lim"})
    @com.yahveh.security.RequiereVista({"nota_entrega", "reportes"})
    public Response generarNotaEntregaPDF(@PathParam("codNotaEntrega") long codNotaEntrega) {
        log.info("Generando PDF de nota de entrega: {}", codNotaEntrega);
        byte[] pdfBytes = notaEntregaService.generarPDF(codNotaEntrega);
        return Response.ok(pdfBytes)
                .header("Content-Disposition", "inline; filename=\"nota_entrega_" + codNotaEntrega + ".pdf\"")
                .build();
    }

    /**
     * Reporte de vendedores en PDF.
     * Query param opcional `codEmpleado` para filtrar por un empleado específico.
     * Sin el param → reporte general de todos los vendedores.
     */
    @GET
    @Path("/vendedores/pdf/{fechaDesde}/{fechaHasta}")
    @Produces("application/pdf")
    @RolesAllowed("admin")
    public Response generarReporteVendedoresPDF(
            @PathParam("fechaDesde") String fechaDesde,
            @PathParam("fechaHasta") String fechaHasta,
            @QueryParam("codEmpleado") Integer codEmpleado) {
        log.info("Generando PDF reporte vendedores: {} - {} (empleado: {})",
                fechaDesde, fechaHasta, codEmpleado);
        byte[] pdfBytes = notaEntregaService.generarReporteVendedores(
                LocalDate.parse(fechaDesde), LocalDate.parse(fechaHasta), codEmpleado);
        String filename = codEmpleado != null
                ? "reporte_vendedor_" + codEmpleado + ".pdf"
                : "reporte_vendedores.pdf";
        return Response.ok(pdfBytes)
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
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

    /**
     * ⭐ Reporte de MOVIMIENTOS de inventario entre fechas.
     * Muestra cada entrada/salida/ajuste con saldo y observación.
     * Query param opcional `codArticulo` para filtrar por un artículo específico.
     */
    @GET
    @Path("/movimientos-inventario/pdf/{fechaDesde}/{fechaHasta}")
    @Produces("application/pdf")
    @RolesAllowed({"admin", "lim"})
    public Response generarReporteMovimientosInventarioPDF(
            @PathParam("fechaDesde") String fechaDesde,
            @PathParam("fechaHasta") String fechaHasta,
            @QueryParam("codArticulo") String codArticulo) {
        log.info("Generando PDF movimientos inventario: {} - {} (art: {})",
                fechaDesde, fechaHasta, codArticulo);
        byte[] pdfBytes = notaEntregaService.generarReporteMovimientosInventario(
                LocalDate.parse(fechaDesde),
                LocalDate.parse(fechaHasta),
                codArticulo);
        String suffix = (codArticulo != null && !codArticulo.isBlank())
                ? "_" + codArticulo
                : "";
        return Response.ok(pdfBytes)
                .header("Content-Disposition",
                        "attachment; filename=\"movimientos_inventario" + suffix + ".pdf\"")
                .build();
    }
}