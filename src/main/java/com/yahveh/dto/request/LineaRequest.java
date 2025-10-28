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
public class LineaRequest {

    @NotNull(message = "La familia es obligatoria")
    private int codFamilia;

    @NotBlank(message = "El nombre de la línea es obligatorio")
    @Size(max = 100, message = "El nombre de la línea no puede exceder 100 caracteres")
    private String linea;

}