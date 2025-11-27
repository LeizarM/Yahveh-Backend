package com.yahveh.repository;


import com.yahveh.exception.BusinessException;
import com.yahveh.model.TelefonoCliente;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class TelefonoClienteRepository extends BaseRepository<TelefonoCliente> {

    public static class TelefonoClienteDetalle {
        public long codTlfCliente;
        public long codCliente;
        public String telefono;
        public String nombreCliente;
        public int audUsuario;
    }

    public static class AbmResult {
        public int error;
        public String errorMsg;
        public int result;

        public boolean isSuccess() {
            return error == 0;
        }
    }

    /**
     * Listar todos los teléfonos
     */
    public List<TelefonoClienteDetalle> listarTodos() {
        String sql = "SELECT * FROM p_list_telefono_cliente(p_accion := 'L')";
        return executeQueryList(sql, this::mapDetalle);
    }

    /**
     * Buscar teléfono por código
     */
    public Optional<TelefonoClienteDetalle> buscarPorCodigo(long codTlfCliente) {
        String sql = "SELECT * FROM p_list_telefono_cliente(p_codtlfcliente := ?, p_accion := 'L')";
        return executeQuerySingle(sql, this::mapDetalle, codTlfCliente);
    }

    /**
     * Listar teléfonos por cliente
     */
    public List<TelefonoClienteDetalle> listarPorCliente(long codCliente) {
        String sql = "SELECT * FROM p_list_telefono_cliente(p_codcliente := ?, p_accion := 'L')";
        return executeQueryList(sql, this::mapDetalle, codCliente);
    }

    /**
     * Crear teléfono
     */
    public int crearTelefono(TelefonoCliente telefono) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_telefono_cliente(" +
                "p_codcliente := ?, " +
                "p_telefono := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                telefono.getCodCliente(),
                telefono.getTelefono(),
                telefono.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear teléfono. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar teléfono
     */
    public void actualizarTelefono(TelefonoCliente telefono) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_telefono_cliente(" +
                "p_codtlfcliente := ?, " +
                "p_telefono := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                telefono.getCodTlfCliente(),
                telefono.getTelefono(),
                telefono.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar teléfono. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar teléfono
     */
    public void eliminarTelefono(Long codTlfCliente, Long audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_telefono_cliente(" +
                "p_codtlfcliente := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codTlfCliente,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar teléfono. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Mapear ResultSet a TelefonoClienteDetalle
     */
    private TelefonoClienteDetalle mapDetalle(ResultSet rs) throws SQLException {
        TelefonoClienteDetalle detalle = new TelefonoClienteDetalle();
        detalle.codTlfCliente = rs.getLong(1);      // cod_tlf_cliente
        detalle.codCliente = rs.getLong(2);         // cod_cliente
        detalle.telefono = rs.getString(3);         // telefono
        detalle.nombreCliente = rs.getString(4);    // nombre_cliente
        detalle.audUsuario = rs.getInt(5);         // aud_usuario
        return detalle;
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        int resultValue = rs.getInt("p_result");
        result.result = rs.wasNull() ? null : resultValue;

        return result;
    }
}