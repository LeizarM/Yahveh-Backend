package com.yahveh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaRequest {
    private String nombres;
    private String apPaterno;
    private String apMaterno;
    private String ciNumero;
    private String ciExpedido; // LP, CB, SC, OR, PT, TJ, CH, BE, PD
    private LocalDate ciFechaVencimiento;
    private String direccion;
    private String estadoCivil; // S=Soltero, C=Casado, D=Divorciado, V=Viudo
    private LocalDate fechaNacimiento;
    private String lugarNacimiento;
    private String sexo; // M=Masculino, F=Femenino
}