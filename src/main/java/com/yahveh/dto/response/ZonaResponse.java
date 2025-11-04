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
public class ZonaResponse {
    private int codZona;
    private int codCiudad;
    private String ciudad;
    private String zona;
    private int totalClientes;
    private int audUsuario;
}