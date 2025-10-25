package com.yahveh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZonaRequest {

    @NotNull(message = "La ciudad es obligatoria")
    private int codCiudad;

    @NotBlank(message = "El nombre de la zona es obligatorio")
    @Size(max = 100, message = "El nombre de la zona no puede exceder 100 caracteres")
    private String zona;
}