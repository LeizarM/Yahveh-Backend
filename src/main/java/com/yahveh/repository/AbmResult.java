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
     * Convierte un long a Integer de forma segura con detección de overflow
     * 
     * Si el valor excede el rango de Integer, registra una advertencia y retorna el valor truncado.
     * Esto permite que la aplicación continúe funcionando mientras se investiga el problema en la base de datos.
     * 
     * @param value valor long del stored procedure
     * @return Integer value (truncado si excede el rango, con advertencia en el log)
     */
    public static Integer safeLongToInteger(long value) {
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            log.warn("Valor {} excede el rango de Integer. Esto puede indicar un problema en la base de datos. " +
                    "El valor será truncado a {}.", value, (int) value);
            return (int) value;
        }
        return (int) value;
    }
}
