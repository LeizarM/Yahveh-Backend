package com.yahveh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa un movimiento de inventario (entrada, salida, ajuste) para reporte.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoInventarioReporteDTO {
    private Long codInventario;
    private LocalDate fecha;
    private String codArticulo;
    private String descripcionArticulo;
    private String lineaArticulo;
    private String tipoMovimiento;     // ENTRADA, SALIDA, AJUSTE
    private Integer cantidad;
    private Integer saldoAnterior;
    private Integer saldoNuevo;
    private BigDecimal precioUnitario;
    private BigDecimal valorTotal;
    private String observacion;
    private String nombreUsuario;
}
