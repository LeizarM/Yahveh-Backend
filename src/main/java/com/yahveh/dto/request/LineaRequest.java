package com.yahveh.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaRequest {

    // Para actualizar (opcional en crear)
    private Long codLinea;

    @NotBlank(message = "El nombre de la línea es requerido")
    private String linea;

    // Métodos helper
    public boolean esCreacion() {
        return codLinea == null;
    }

    public boolean esActualizacion() {
        return codLinea != null;
    }
}