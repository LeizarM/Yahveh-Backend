package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloResponse {
    private String codArticulo;
    private int codLinea;
    private String linea;
    private String descripcion;
    private String descripcion2;
    private int stockActual;
    private double precioActual;
    private int audUsuario;
}