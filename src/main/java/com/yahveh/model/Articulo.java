package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Articulo {
    private String codArticulo;
    private Integer codLinea;
    private String descripcion;
    private String descripcion2;
    private Long audUsuario;
   // private LocalDateTime audFecha;
}