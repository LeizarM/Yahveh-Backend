package com.yahveh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendedorReporteDTO {
    private Long codEmpleado;
    private String nombreVendedor;
    private LocalDate fecha;
    private Long codNotaEntrega;
    private String nombreCliente;
    private String nit;
    private String direccion;
    private String zona;
    private Integer cantidadArticulos;
    private Float totalBs;
}
