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
public class TelefonoClienteRequest {

    @NotNull(message = "El código de cliente es obligatorio")
    private long codCliente;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
}