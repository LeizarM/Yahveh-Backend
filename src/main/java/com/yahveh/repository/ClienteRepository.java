package com.yahveh.repository;

import com.yahveh.dto.response.ClienteResponse;
import com.yahveh.model.Cliente;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClienteRepository extends BaseRepository<Cliente> {

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
     * Crear nuevo cliente
     */
    public int crearCliente(Cliente cliente) {
        String sql = "SELECT p_abm_cliente(" +
                "p_codzona := ?, " +
                "p_nit := ?, " +
                "p_razonsocial := ?, " +
                "p_nombrecliente := ?, " +
                "p_direccion := ?, " +
                "p_referencia := ?, " +
                "p_obs := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                cliente.getCodZona(),
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getNombreCliente(),
                cliente.getDireccion(),
                cliente.getReferencia(),
                cliente.getObs(),
                cliente.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al crear cliente"));
    }

    /**
     * Actualizar cliente
     */
    public void actualizarCliente(Cliente cliente) {
        String sql = "SELECT p_abm_cliente(" +
                "p_codcliente := ?, " +
                "p_codzona := ?, " +
                "p_nit := ?, " +
                "p_razonsocial := ?, " +
                "p_nombrecliente := ?, " +
                "p_direccion := ?, " +
                "p_referencia := ?, " +
                "p_obs := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                cliente.getCodCliente(),
                cliente.getCodZona(),
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getNombreCliente(),
                cliente.getDireccion(),
                cliente.getReferencia(),
                cliente.getObs(),
                cliente.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar cliente"));
    }

    /**
     * Eliminar cliente
     */
    public void eliminarCliente(int codCliente, int audUsuario) {
        String sql = "SELECT p_abm_cliente(" +
                "p_codcliente := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                codCliente,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar cliente"));
    }

    /**
     * Mapear ResultSet a ClienteResponse (con todos los datos del SP)
     */
    private ClienteResponse mapClienteResponse(ResultSet rs) throws SQLException {
        return ClienteResponse.builder()
                .codCliente(rs.getLong(1))
                .codZona(rs.getLong(2))
                .zona(rs.getString(3))
                .nit(rs.getString(4))
                .razonSocial(rs.getString(5))
                .nombreCliente(rs.getString(6))
                .direccion(rs.getString(7))
                .referencia(rs.getString(8))
                .obs(rs.getString(9))
                .totalNotas(rs.getInt(10))
                .audUsuario(rs.getInt(11))
                .build();
    }
}