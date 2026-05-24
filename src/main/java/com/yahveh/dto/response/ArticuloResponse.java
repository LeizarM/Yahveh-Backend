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
    private double precioSinFactura;
    private int audUsuario;
    /** Numero de fila global (1..N) calculado por el SP. Útil para enumerar incluso paginando. */
    private long rowNumber;
}