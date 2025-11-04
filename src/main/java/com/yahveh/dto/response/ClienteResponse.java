package com.yahveh.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteResponse {
    private int codCliente;
    private int codZona;
    private String zona;
    private String nit;
    private String razonSocial;
    private String nombreCliente;
    private String direccion;
    private String referencia;
    private String obs;
    private int telefono;
    private int totalNotas;
    private int audUsuario;
}