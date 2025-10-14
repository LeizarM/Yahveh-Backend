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
public class Empleado {
    private Long codEmpleado;
    private Long codPersona;
    private Long audUsuario;
    //private LocalDateTime audFecha;
}