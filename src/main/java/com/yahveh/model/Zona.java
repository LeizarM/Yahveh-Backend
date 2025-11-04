package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zona {
    private int codZona;
    private int codCiudad;
    private String zona;
    private int audUsuario;
}