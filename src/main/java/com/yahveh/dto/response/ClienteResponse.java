package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {
    private Long codCliente;
    private Long codZona;
    private String nit;
    private String razonSocial;
    private String nombreCliente;
    private String direccion;
    private String referencia;
    private String obs;
}