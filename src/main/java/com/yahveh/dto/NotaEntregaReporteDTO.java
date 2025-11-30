package com.yahveh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaEntregaReporteDTO {

    // Datos del encabezado
    private long codNotaEntrega;
    private LocalDate fecha;
    private long codCliente;
    private String nombreCliente;
    private String nit;
    private String razonSocial;
    private String direccion;
    private String zona;
    private String telefonos;

    // Totales
    private float totalConFactura;
    private float totalSinFactura;
    private int totalArticulos;

    // Detalle de artículos
    private List<DetalleArticuloDTO> detalles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleArticuloDTO {
        private String codArticulo;
        private String lineaArticulo;
        private String descripcionArticulo;
        private int cantidad;
        private float precioUnitario;
        private float precioTotal;
        private float precioSinFactura;
        private float subtotalSinFactura;
        private String tipoCopia; // NUEVO CAMPO
    }
}