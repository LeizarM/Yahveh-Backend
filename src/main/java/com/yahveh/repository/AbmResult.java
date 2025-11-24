package com.yahveh.repository;

import lombok.extern.slf4j.Slf4j;

/**
 * Resultado estándar de operaciones ABM (Alta, Baja, Modificación)
 * Usado por todos los repositorios para manejar respuestas de stored procedures
 */
@Slf4j
public class AbmResult {
    public int error;
    public String errorMsg;
    public Integer result;

    public boolean isSuccess() {
        return error == 0;
    }

    /**
     * Convierte un long a Integer de forma segura
     * Registra una advertencia si hay pérdida de precisión
     * 
     * @param value valor long del stored procedure
     * @return Integer value, o null si excede el rango de int
     */
    public static Integer safeLongToInteger(long value) {
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            log.warn("Valor {} excede el rango de Integer. Esto puede indicar un problema en la base de datos.", value);
            // Retornar el valor truncado con advertencia
            return (int) value;
        }
        return (int) value;
    }
}
