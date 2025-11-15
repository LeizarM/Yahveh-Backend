package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponse {
    private int codInventario;
    private String codArticulo;
    private String descripcionArticulo;
    private String descripcion2Articulo;
    private String lineaArticulo;
    private String tipoMovimiento;
    private int cantidad;
    private int saldoAnterior;
    private int saldoNuevo;
    private float precioUnitario;
    private float valorTotal;
    private LocalDate fecha;
    private String observacion;
    private int audUsuario;

}