package com.yahveh.repository;

import com.yahveh.exception.BusinessException;
import com.yahveh.model.Empleado;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class EmpleadoRepository extends BaseRepository<Empleado> {


    public static class EmpleadoDetalle {
        public Long codEmpleado;
        public Long codPersona;
        public String nombres;
        public String apPaterno;
        public String apMaterno;
        public String nombreCompleto;
        public String ciNumero;
        public String ciExpedido;
        public String sexo;
        public Long audUsuario;
    }

    /**
     * Listar todos los empleados
     */
    public List<EmpleadoDetalle> listarTodos() {
        String sql = "SELECT * FROM p_list_empleado(p_accion := 'L')";
        return executeQueryList(sql, this::mapEmpleadoDetalle);
    }

    /**
     * Buscar empleado por código
     */
    public Optional<EmpleadoDetalle> buscarPorCodigo(long codEmpleado) {
        String sql = "SELECT * FROM p_list_empleado(p_codempleado := ?, p_accion := 'L')";
        return executeQuerySingle(sql, this::mapEmpleadoDetalle, codEmpleado);
    }

    /**
     * Buscar empleado por persona
     */
    public Optional<EmpleadoDetalle> buscarPorPersona(long codPersona) {
        String sql = "SELECT * FROM p_list_empleado(p_codpersona := ?, p_accion := 'L')";
        return executeQuerySingle(sql, this::mapEmpleadoDetalle, codPersona);
    }

    /**
     * Buscar empleados por nombre
     */
    public List<EmpleadoDetalle> buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM p_list_empleado(p_nombres := ?, p_accion := 'L')";
        return executeQueryList(sql, this::mapEmpleadoDetalle, nombre);
    }

    /**
     * Crear nuevo empleado
     */
    public long crearEmpleado(Empleado empleado) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_empleado(" +
                "p_codpersona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                empleado.getCodPersona(),
                empleado.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear empleado. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar empleado
     */
    public void actualizarEmpleado(Empleado empleado) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_empleado(" +
                "p_codempleado := ?, " +
                "p_codpersona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                empleado.getCodEmpleado(),
                empleado.getCodPersona(),
                empleado.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar empleado. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar empleado
     */
    public void eliminarEmpleado(long codEmpleado, long audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_empleado(" +
                "p_codempleado := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codEmpleado,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar empleado. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Mapear ResultSet a EmpleadoDetalle
     */
    private EmpleadoDetalle mapEmpleadoDetalle(ResultSet rs) throws SQLException {
        EmpleadoDetalle detalle = new EmpleadoDetalle();
        detalle.codEmpleado = rs.getLong(1);
        detalle.codPersona = rs.getLong(2);
        detalle.nombres = rs.getString(3);
        detalle.apPaterno = rs.getString(4);
        detalle.apMaterno = rs.getString(5);
        detalle.nombreCompleto = rs.getString(6);
        detalle.ciNumero = rs.getString(7);
        detalle.ciExpedido = rs.getString(8);
        detalle.sexo = rs.getString(9);
        detalle.audUsuario = rs.getLong(10);
        return detalle;
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        long resultValue = rs.getLong("p_result");
        result.result = rs.wasNull() ? null : (int) resultValue;

        return result;
    }
}