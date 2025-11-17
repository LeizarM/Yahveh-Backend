package com.yahveh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleNotaEntregaRequest {
    private String codArticulo;
    private int cantidad;
    private float precioUnitario;
    private float precioSinFactura;
}