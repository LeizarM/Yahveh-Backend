package com.yahveh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaEntrega {
    private Long codNotaEntrega;
    private Long codCliente;
    private String nombreCliente;
    private LocalDate fecha;
    private String direccion;
    private String zona;
    private Long audUsuario;
    //private LocalDateTime audFecha;
    private List<DetalleNotaEntrega> detalles;
}