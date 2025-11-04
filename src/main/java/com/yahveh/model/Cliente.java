package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private int codCliente;
    private int codZona;
    private String nit;
    private String razonSocial;
    private String nombreCliente;
    private String direccion;
    private String referencia;
    private String obs;
    private int audUsuario;
}