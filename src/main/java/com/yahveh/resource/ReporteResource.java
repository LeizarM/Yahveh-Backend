package com.yahveh.resource;

import com.yahveh.service.NotaEntregaService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Path("/api/reportes")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ReporteResource {

    @Inject
    NotaEntregaService notaEntregaService;

    @GET
    @Path("/nota-entrega/{codNotaEntrega}")
    @Produces("application/pdf")
    @RolesAllowed({"admin", "user"})
    public Response generarNotaEntregaPDF(@PathParam("codNotaEntrega") long codNotaEntrega) {
        log.info("Generando PDF de nota de entrega: {}", codNotaEntrega);

        byte[] pdfBytes = notaEntregaService.generarPDF(codNotaEntrega);

        return Response.ok(pdfBytes)
                .header("Content-Disposition", "inline; filename=\"nota_entrega_" + codNotaEntrega + ".pdf\"")
                .build();
    }
}