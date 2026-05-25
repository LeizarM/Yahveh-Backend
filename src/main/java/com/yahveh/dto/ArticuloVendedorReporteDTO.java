package com.yahveh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Detalle de cada artículo vendido por un vendedor, para el reporte.
 * Permite agrupar por vendedor → notas → artículos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloVendedorReporteDTO {
    private Long codEmpleado;
    private String nombreVendedor;
    private Long codNotaEntrega;
    private LocalDate fecha;
    private String nombreCliente;
    private String nit;
    private String direccion;
    private String zona;
    private Long codDetalle;
    private String codArticulo;
    private String descripcionArticulo;
    private String lineaArticulo;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;          // %
    private BigDecimal precioConDescuento;
    private BigDecimal subtotal;
}
