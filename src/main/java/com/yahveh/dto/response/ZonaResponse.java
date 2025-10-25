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
    private Long codZona;
    private Long codCiudad;
    private String ciudad;
    private String zona;
    private Integer totalClientes;
    private Integer audUsuario;
}