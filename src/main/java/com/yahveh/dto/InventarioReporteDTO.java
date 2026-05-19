package com.yahveh.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioReporteDTO {
    private String codArticulo;
    private String descripcion;
    private String codLinea;
    private String descripcionLinea;
    private String codFamilia;
    private String descripcionFamilia;
    private Integer entradas;
    private Integer salidas;
    private Integer stockActual;
}
