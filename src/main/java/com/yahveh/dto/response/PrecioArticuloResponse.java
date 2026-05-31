package com.yahveh.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrecioArticuloResponse {
    private int codPrecio;
    private String codArticulo;
    private String descripcionArticulo;
    private String linea;
    private int listaPrecio;
    private BigDecimal precioBase;
    private BigDecimal precio;
    private BigDecimal precioSinFactura;

}