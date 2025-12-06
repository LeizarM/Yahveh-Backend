package com.yahveh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para el Reporte de Ventas Mensual
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaReporteDTO {

    // Datos de la venta
    private LocalDate fecha;
    private Long codCliente;
    private String nombreCliente;
    private String direccion;
    private String ciudad;

    // Datos del producto
    private String codArticulo;
    private Integer cantidad;
    private String lineaArticulo;
    private String productoCompleto;

    // Precios y cálculos
    private Float precioUnitario;
    private Float descuento;
    private Float totalBs;
    private Float descBs;
    private Float bsUnitario;
    private Float totalBsDesc;
    private Float totalGeneralBs;

    // Campo para identificar la fila de totales
    private String tipoFila; // "DETALLE" o "TOTAL"
}
