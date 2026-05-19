package com.yahveh.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioVistasRequest {
    /** Lista de codVista que se asignarán al usuario (reemplaza todo lo anterior). */
    private List<Long> codVistas;
}
