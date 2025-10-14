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
public class Usuario {

    private Long codUsuario;
    private Long codEmpleado;
    private String login;
    private String password;
    private String tipoUsuario;
    private String estado;
    private Long audUsuario;
    //private LocalDateTime audFecha;

}