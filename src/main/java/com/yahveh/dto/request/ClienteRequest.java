package com.yahveh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequest {

    @NotNull(message = "La zona es obligatoria")
    private int codZona;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 20, message = "El NIT no puede exceder 20 caracteres")
    private String nit;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 100, message = "La razón social no puede exceder 100 caracteres")
    private String razonSocial;

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombreCliente;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String direccion;


    private String referencia;

    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String obs;

    /*@NotNull(message = "El teléfono es obligatorio")
    @Positive(message = "El teléfono debe ser un número positivo")
    private Integer telefono;*/
}