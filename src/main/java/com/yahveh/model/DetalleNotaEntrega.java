package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleNotaEntrega {
    private int codDetalle;
    private int codNotaEntrega;
    private String codArticulo;
    private String descripcionArticulo;
    private String descripcion2Articulo;
    private String lineaArticulo;
    private int codLinea;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal precioTotal;
    private BigDecimal precioSinFactura;
    private float descuento;          // porcentaje (0-100), no es dinero
    private BigDecimal precioConDescuento;
    private int audUsuario;
}