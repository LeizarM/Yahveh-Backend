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
public class TelefonoCliente {
    private long codTlfCliente;
    private long codCliente;
    private String telefono;
    private int audUsuario;

}