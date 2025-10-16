package com.yahveh.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vista {

    private long codVista;
    private long codVistaPadre;
    private String direccion;
    private String titulo;
    private Long audUsuario;
}
