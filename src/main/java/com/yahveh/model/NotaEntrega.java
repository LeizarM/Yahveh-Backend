package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaEntrega {
    private int codNotaEntrega;
    private int codCliente;
    private String nombreCliente;
    private LocalDate fecha;
    private String direccion;
    private String zona;
    private int estado;
    private String estadoTexto;
    private int audUsuario;
}