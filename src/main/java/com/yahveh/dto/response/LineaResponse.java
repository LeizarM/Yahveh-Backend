package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaResponse {
    private int codLinea;
    private String linea;
    private int totalArticulos;
    private int articulos_activos;
    private int audUsuario;
}