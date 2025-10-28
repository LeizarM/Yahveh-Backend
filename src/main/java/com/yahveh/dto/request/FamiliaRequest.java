package com.yahveh.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamiliaRequest {

    @NotBlank(message = "El nombre de la familia es obligatorio")
    @Size(max = 100, message = "El nombre de la familia no puede exceder 100 caracteres")
    private String familia;
}