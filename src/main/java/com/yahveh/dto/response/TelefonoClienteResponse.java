package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelefonoClienteResponse {
    private long codTlfCliente;
    private long codCliente;
    private String telefono;
    private String nombreCliente;  // Para mostrar en listados
    private int audUsuario;
}