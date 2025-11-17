package com.yahveh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaEntregaResponse {
    private int codNotaEntrega;
    private int codCliente;
    private String nombreCliente;
    private LocalDate fecha;
    private String direccion;
    private String zona;
    private int audUsuario;
    private LocalDateTime audFecha;
    private List<DetalleNotaEntregaResponse> detalles;
    private float totalGeneral;
    private int totalArticulos;
}