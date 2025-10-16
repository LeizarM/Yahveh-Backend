package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VistaResponse {

    private Long codVista;
    private Long codVistaPadre;
    private String direccion;
    private String titulo;

}