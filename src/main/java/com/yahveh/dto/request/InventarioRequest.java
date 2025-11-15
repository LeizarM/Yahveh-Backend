package com.yahveh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {
    private String codArticulo;
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE, INVENTARIO_INICIAL
    private int cantidad;
    private float precioUnitario;
    private LocalDate fecha;
    private String observacion;
}