package com.yahveh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaEntregaRequest {
    private int codCliente;
    private LocalDate fecha;
    private String direccion;
    private String zona;
    private List<DetalleNotaEntregaRequest> detalles;
}