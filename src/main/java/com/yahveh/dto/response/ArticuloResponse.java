package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private BigDecimal precioActual;
    private BigDecimal precioSinFactura;
    private int audUsuario;
    /** Numero de fila global (1..N) calculado por el SP. Útil para enumerar incluso paginando. */
    private long rowNumber;
}