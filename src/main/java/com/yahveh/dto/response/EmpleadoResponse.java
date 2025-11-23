package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpleadoResponse {
    private long codEmpleado;
    private long codPersona;
    private String nombres;
    private String apPaterno;
    private String apMaterno;
    private String nombreCompleto;
    private String ciNumero;
    private String ciExpedido;
    private String ciCompleto;
    private String sexo;
    private String sexoDescripcion;
    private long audUsuario;

}