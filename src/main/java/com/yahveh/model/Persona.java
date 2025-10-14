package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    private Long codPersona;
    private String nombres;
    private String apPaterno;
    private String apMaterno;
    private String ciNumero;
    private String ciExpedido;
    private LocalDate ciFechaVencimiento;
    private String direccion;
    private String estadoCivil;
    private LocalDate fechaNacimiento;
    private String lugarNacimiento;
    private String sexo;
    private Long audUsuario;
    //private LocalDateTime audFecha;
}