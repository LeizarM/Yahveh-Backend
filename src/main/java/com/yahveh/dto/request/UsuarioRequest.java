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
public class UsuarioRequest {

    // Para actualizar (opcional en crear)
    private Long codUsuario;

    @NotNull(message = "El código de empleado es requerido")
    private Long codEmpleado;

    @NotBlank(message = "El login es requerido")
    private String login;

    // Opcional: solo requerido al crear, opcional al actualizar
    private String password;

    @NotBlank(message = "El tipo de usuario es requerido")
    private String tipoUsuario;

    private String estado; // Default: "A" (Activo)

    // Métodos de validación personalizados
    public boolean esCreacion() {
        return codUsuario == null;
    }

    public boolean esActualizacion() {
        return codUsuario != null;
    }

    public boolean cambiaPassword() {
        return password != null && !password.trim().isEmpty();
    }
}