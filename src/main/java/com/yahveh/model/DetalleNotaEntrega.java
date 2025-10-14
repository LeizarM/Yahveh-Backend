package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleNotaEntrega {
    private Long codDetalle;
    private Long codNotaEntrega;
    private String codArticulo;
    private String descripcionArticulo;
    private String descripcion2Articulo;
    private String lineaArticulo;
    private Integer codLinea;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal precioTotal;
    private BigDecimal precioSinFactura;
    private Long audUsuario;
    //private LocalDateTime audFecha;
}