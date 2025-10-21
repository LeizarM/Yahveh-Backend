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
public class ArticuloRequest {

    // Para actualizar (en crear es generado o proporcionado)
    @NotBlank(message = "El código del artículo es requerido")
    private String codArticulo;

    @NotNull(message = "El código de línea es requerido")
    private int codLinea;

    @NotBlank(message = "La descripción es requerida")
    private String descripcion;

    private String descripcion2;

    // Métodos helper
    public boolean tieneDescripcion2() {
        return descripcion2 != null && !descripcion2.trim().isEmpty();
    }
}