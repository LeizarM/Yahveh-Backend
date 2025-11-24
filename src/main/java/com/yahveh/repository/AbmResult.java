package com.yahveh.repository;

/**
 * Resultado estándar de operaciones ABM (Alta, Baja, Modificación)
 * Usado por todos los repositorios para manejar respuestas de stored procedures
 */
public class AbmResult {
    public int error;
    public String errorMsg;
    public Integer result;

    public boolean isSuccess() {
        return error == 0;
    }
}
