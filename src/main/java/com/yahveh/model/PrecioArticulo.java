package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrecioArticulo {
    private int codPrecio;
    private String codArticulo;
    private Integer listaPrecio;
    private float precioBase;
    private float precio;
    private float precioSinFactura;
    private int audUsuario;  // ✅ Cambiar a Long para coincidir con BIGINT
}