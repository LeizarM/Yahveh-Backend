package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleNotaEntregaResponse {
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
    private int audUsuario;
    private LocalDateTime audFecha;
}