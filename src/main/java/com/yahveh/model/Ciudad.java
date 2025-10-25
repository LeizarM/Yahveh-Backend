package com.yahveh.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ciudad {

    private int codCiudad;
    private int codPais;
    private String ciudad;
    private int audUsuario;
}
