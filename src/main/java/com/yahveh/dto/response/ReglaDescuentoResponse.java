package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReglaDescuentoResponse {
    private long codReglaDescuento;
    private String codArticulo;
    private String descripcionArticulo;
    private int cantidadMinima;
    private float descuento;
    private long audUsuario;
}
