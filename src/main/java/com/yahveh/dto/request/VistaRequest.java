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
public class VistaRequest {

    // Para actualizar (opcional en crear)
    private Long codVista;

    // Puede ser null para vistas raíz (menú principal)
    private Long codVistaPadre;

    @NotBlank(message = "La dirección es requerida")
    private String direccion;

    @NotBlank(message = "El título es requerido")
    private String titulo;

}