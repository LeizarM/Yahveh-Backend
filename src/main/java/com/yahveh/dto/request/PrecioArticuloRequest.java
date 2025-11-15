package com.yahveh.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrecioArticuloRequest {

    @NotBlank(message = "El código de artículo es obligatorio")
    private String codArticulo;

    @NotNull(message = "La lista de precio es obligatoria")
    private int listaPrecio;

    @NotNull(message = "El precio base es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio base debe ser mayor o igual a 0")
    private float precioBase;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    private float precio;

    @NotNull(message = "El precio sin factura es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio sin factura debe ser mayor o igual a 0")
    private float precioSinFactura;
}