package com.yahveh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleNotaEntregaRequest {
    private String codArticulo;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal precioSinFactura;
    private float descuento;          // porcentaje (0-100), no es dinero
}