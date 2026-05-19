package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReglaDescuento {
    private long codReglaDescuento;
    private String codArticulo;
    private String descripcionArticulo;
    private int cantidadMinima;
    private float descuento;
    private long audUsuario;
}
