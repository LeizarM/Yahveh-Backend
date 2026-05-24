package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private float precioUnitario;
    private float precioTotal;
    private float precioSinFactura;
    private float descuento;
    private float precioConDescuento;
    private int audUsuario;
}