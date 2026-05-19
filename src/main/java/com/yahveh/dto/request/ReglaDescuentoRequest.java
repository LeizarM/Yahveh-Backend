package com.yahveh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglaDescuentoRequest {
    private String codArticulo;
    private int cantidadMinima;
    private float descuento;
}
