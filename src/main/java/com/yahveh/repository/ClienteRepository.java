package com.yahveh.repository;

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

    public List<Cliente> listarTodos() {
        String sql = "SELECT * FROM sp_listar_clientes()";
        return executeQueryList(sql, this::mapCliente);
    }

    public Optional<Cliente> buscarPorId(Long codCliente) {
        String sql = "SELECT * FROM sp_buscar_cliente(?)";
        return executeQuerySingle(sql, this::mapCliente, codCliente);
    }

    public List<Cliente> buscarPorZona(Long codZona) {
        String sql = "SELECT * FROM sp_listar_clientes_por_zona(?)";
        return executeQueryList(sql, this::mapCliente, codZona);
    }

    public Long crearCliente(Cliente cliente, Long audUsuario) {
        String sql = "SELECT sp_crear_cliente(?, ?, ?, ?, ?, ?, ?, ?)";

        return executeQuerySingle(sql, rs -> rs.getLong(1),
                cliente.getCodZona(),
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getNombreCliente(),
                cliente.getDireccion(),
                cliente.getReferencia(),
                cliente.getObs(),
                audUsuario
        ).orElse(null);
    }

    public boolean actualizarCliente(Cliente cliente, Long audUsuario) {
        String sql = "SELECT sp_actualizar_cliente(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return executeQuerySingle(sql, rs -> rs.getBoolean(1),
                cliente.getCodCliente(),
                cliente.getCodZona(),
                cliente.getNit(),
                cliente.getRazonSocial(),
                cliente.getNombreCliente(),
                cliente.getDireccion(),
                cliente.getReferencia(),
                cliente.getObs(),
                audUsuario
        ).orElse(false);
    }

    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return Cliente.builder()
                .codCliente(rs.getLong("cod_cliente"))
                .codZona(rs.getLong("cod_zona"))
                .nit(rs.getString("nit"))
                .razonSocial(rs.getString("razon_social"))
                .nombreCliente(rs.getString("nombre_cliente"))
                .direccion(rs.getString("direccion"))
                .referencia(rs.getString("referencia"))
                .obs(rs.getString("obs"))
                .build();
    }
}