package com.yahveh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearClienteRequest {
    @NotNull(message = "La zona es requerida")
    private Long codZona;

    @NotBlank(message = "El NIT es requerido")
    private String nit;

    private String razonSocial;

    @NotBlank(message = "El nombre del cliente es requerido")
    private String nombreCliente;

    @NotBlank(message = "La dirección es requerida")
    private String direccion;

    private String referencia;
    private String obs;
}