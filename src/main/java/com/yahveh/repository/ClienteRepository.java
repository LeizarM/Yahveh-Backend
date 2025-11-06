package com.yahveh.repository;

import com.yahveh.dto.response.ClienteResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Cliente;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ClienteRepository extends BaseRepository<Cliente> {

    /**
     * Resultado de operación ABM
     */
    public static class AbmResult {
        public int error;
        public String errorMsg;
        public int result;

        public boolean isSuccess() {
            return error == 0;
        }
    }

    /**
     * Listar todos los clientes con información completa
     */
    public List<ClienteResponse> listarTodosCompleto() {
        String sql = "SELECT cod_cliente, cod_zona, zona, nit, razon_social, nombre_cliente, " +
                "direccion, referencia, obs, total_notas, aud_usuario " +
                "FROM p_list_cliente(p_accion := ?)";
        return executeQueryList(sql, this::mapClienteResponse, "L");
    }

    /**
     * Buscar cliente por ID con información completa
     */
    public Optional<ClienteResponse> buscarPorIdCompleto(int codCliente) {
        String sql = "SELECT cod_cliente, cod_zona, zona, nit, razon_social, nombre_cliente, " +
                "direccion, referencia, obs, total_notas, aud_usuario " +
                "FROM p_list_cliente(p_codcliente := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapClienteResponse, codCliente, "L");
    }

    /**
     * Listar clientes por zona con información completa
     */
    public List<ClienteResponse> listarPorZonaCompleto(int codZona) {
        String sql = "SELECT cod_cliente, cod_zona, zona, nit, razon_social, nombre_cliente, " +
                "direccion, referencia, obs, total_notas, aud_usuario " +
                "FROM p_list_cliente(p_codzona := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapClienteResponse, codZona, "L");
    }

    /**
     * Buscar clientes por NIT con información completa
     */
    public List<ClienteResponse> buscarPorNitCompleto(String nit) {
        String sql = "SELECT cod_cliente, cod_zona, zona, nit, razon_social, nombre_cliente, " +
                "direccion, referencia, obs, total_notas, aud_usuario " +
                "FROM p_list_cliente(p_nit := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapClienteResponse, nit, "L");
    }

    /**
     * Buscar clientes por nombre con información completa
     */
    public List<ClienteResponse> buscarPorNombreCompleto(String nombre) {
        String sql = "SELECT cod_cliente, cod_zona, zona, nit, razon_social, nombre_cliente, " +
                "direccion, referencia, obs, total_notas, aud_usuario " +
                "FROM p_list_cliente(p_nombre := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapClienteResponse, nombre, "L");
    }

    /**
     * Verificar si existe un cliente
     */
    public boolean existeCliente(int codCliente) {
        return buscarPorIdCompleto(codCliente).isPresent();
    }

    /**
     * Crear nuevo cliente con manejo de errores
     */
    public int crearCliente(Cliente cliente) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_cliente(" +
                "p_codzona := ?, " +
                "p_nit := ?, " +
                "p_razonsocial := ?, " +
                "p_nombrecliente := ?, " +
                "p_direccion := ?, " +
                "p_referencia := ?, " +
                "p_obs := ?, " +
               // "p_telefono := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                cliente.getCodZona(),
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getNombreCliente(),
                cliente.getDireccion(),
                cliente.getReferencia(),
                cliente.getObs(),
                cliente.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_cliente"));

        if (!result.isSuccess()) {
            log.error("Error al crear cliente. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Cliente creado exitosamente con ID: {}", result.result);
        return result.result;
    }

    /**
     * Actualizar cliente con manejo de errores
     */
    public void actualizarCliente(Cliente cliente) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_cliente(" +
                "p_codcliente := ?, " +
                "p_codzona := ?, " +
                "p_nit := ?, " +
                "p_razonsocial := ?, " +
                "p_nombrecliente := ?, " +
                "p_direccion := ?, " +
                "p_referencia := ?, " +
                "p_obs := ?, " +
               // "p_telefono := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                cliente.getCodCliente(),
                cliente.getCodZona(),
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getNombreCliente(),
                cliente.getDireccion(),
                cliente.getReferencia(),
                cliente.getObs(),
                cliente.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_cliente"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar cliente. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Cliente actualizado exitosamente: {}", cliente.getCodCliente());
    }

    /**
     * Eliminar cliente con manejo de errores
     */
    public void eliminarCliente(int codCliente, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_cliente(" +
                "p_codcliente := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codCliente,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_cliente"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar cliente. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Cliente eliminado exitosamente: {}", codCliente);
    }

    /**
     * Mapear ResultSet a ClienteResponse
     */
    private ClienteResponse mapClienteResponse(ResultSet rs) throws SQLException {
        return ClienteResponse.builder()
                .codCliente(rs.getInt(1))
                .codZona(rs.getInt(2))
                .zona(rs.getString(3))
                .nit(rs.getString(4))
                .razonSocial(rs.getString(5))
                .nombreCliente(rs.getString(6))
                .direccion(rs.getString(7))
                .referencia(rs.getString(8))
                .obs(rs.getString(9))
                //.telefono(rs.getInt(10))
                .totalNotas(rs.getInt(10))
                .audUsuario(rs.getInt(11))
                .build();
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        // Manejar el caso donde p_result puede ser NULL
        int resultValue = rs.getInt("p_result");
        result.result = rs.wasNull() ? null : resultValue;

        return result;
    }
}