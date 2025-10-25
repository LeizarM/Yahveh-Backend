package com.yahveh.repository;

import com.yahveh.dto.response.ZonaResponse;
import com.yahveh.model.Zona;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ZonaRepository extends BaseRepository<Zona> {

    /**
     * Listar todas las zonas con información completa
     */
    public List<ZonaResponse> listarTodasCompleto() {
        String sql = "SELECT cod_zona, cod_ciudad, ciudad, zona, total_clientes, aud_usuario " +
                "FROM p_list_zona(p_accion := ?)";
        return executeQueryList(sql, this::mapZonaResponse, "L");
    }

    /**
     * Buscar zona por ID con información completa
     */
    public Optional<ZonaResponse> buscarPorIdCompleto(int codZona) {
        String sql = "SELECT cod_zona, cod_ciudad, ciudad, zona, total_clientes, aud_usuario " +
                "FROM p_list_zona(p_codzona := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapZonaResponse, codZona, "L");
    }

    /**
     * Listar zonas por ciudad con información completa
     */
    public List<ZonaResponse> listarPorCiudadCompleto(int codCiudad) {
        String sql = "SELECT cod_zona, cod_ciudad, ciudad, zona, total_clientes, aud_usuario " +
                "FROM p_list_zona(p_codciudad := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapZonaResponse, codCiudad, "L");
    }

    /**
     * Buscar zonas por nombre con información completa
     */
    public List<ZonaResponse> buscarPorNombreCompleto(String zona) {
        String sql = "SELECT cod_zona, cod_ciudad, ciudad, zona, total_clientes, aud_usuario " +
                "FROM p_list_zona(p_zona := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapZonaResponse, zona, "L");
    }

    /**
     * Verificar si existe una zona
     */
    public boolean existeZona(int codZona) {
        return buscarPorIdCompleto(codZona).isPresent();
    }

    /**
     * Buscar zona por ID (solo para validaciones)
     */
    public Optional<Zona> buscarPorId(int codZona) {
        String sql = "SELECT cod_zona, cod_ciudad, ciudad, zona, total_clientes, aud_usuario " +
                "FROM p_list_zona(p_codzona := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapZona, codZona, "L");
    }

    /**
     * Crear nueva zona
     */
    public int crearZona(Zona zona) {
        String sql = "SELECT p_abm_zona(" +
                "p_codciudad := ?, " +
                "p_zona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                zona.getCodCiudad(),
                zona.getZona(),
                zona.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al crear zona"));
    }

    /**
     * Actualizar zona
     */
    public void actualizarZona(Zona zona) {
        String sql = "SELECT p_abm_zona(" +
                "p_codzona := ?, " +
                "p_codciudad := ?, " +
                "p_zona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                zona.getCodZona(),
                zona.getCodCiudad(),
                zona.getZona(),
                zona.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar zona"));
    }

    /**
     * Eliminar zona
     */
    public void eliminarZona(int codZona, int audUsuario) {
        String sql = "SELECT p_abm_zona(" +
                "p_codzona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                codZona,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar zona"));
    }

    /**
     * Mapear ResultSet a ZonaResponse (con todos los datos del SP)
     */
    private ZonaResponse mapZonaResponse(ResultSet rs) throws SQLException {
        return ZonaResponse.builder()
                .codZona(rs.getLong(1))
                .codCiudad(rs.getLong(2))
                .ciudad(rs.getString(3))
                .zona(rs.getString(4))
                .totalClientes(rs.getInt(5))
                .audUsuario(rs.getInt(6))
                .build();
    }

    /**
     * Mapear ResultSet a Zona (solo para validaciones)
     */
    private Zona mapZona(ResultSet rs) throws SQLException {
        return Zona.builder()
                .codZona(rs.getLong(1))
                .codCiudad(rs.getLong(2))
                .zona(rs.getString(3))
                .audUsuario(rs.getInt(4))
                .build();
    }
}