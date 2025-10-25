package com.yahveh.repository;

import com.yahveh.dto.response.CiudadResponse;
import com.yahveh.model.Ciudad;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CiudadRepository extends BaseRepository<Ciudad> {

    /**
     * Listar todas las ciudades con información completa
     */
    public List<CiudadResponse> listarTodasCompleto() {
        String sql = "SELECT cod_ciudad, cod_pais, pais, ciudad, total_zonas, aud_usuario " +
                "FROM p_list_ciudad(p_accion := ?)";
        return executeQueryList(sql, this::mapCiudadResponse, "L");
    }

    /**
     * Buscar ciudad por ID con información completa
     */
    public Optional<CiudadResponse> buscarPorIdCompleto(int codCiudad) {
        String sql = "SELECT cod_ciudad, cod_pais, pais, ciudad, total_zonas, aud_usuario " +
                "FROM p_list_ciudad(p_codciudad := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapCiudadResponse, codCiudad, "L");
    }

    /**
     * Listar ciudades por país con información completa
     */
    public List<CiudadResponse> listarPorPaisCompleto(int codPais) {
        String sql = "SELECT cod_ciudad, cod_pais, pais, ciudad, total_zonas, aud_usuario " +
                "FROM p_list_ciudad(p_codpais := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapCiudadResponse, codPais, "L");
    }

    /**
     * Buscar ciudades por nombre con información completa
     */
    public List<CiudadResponse> buscarPorNombreCompleto(String ciudad) {
        String sql = "SELECT cod_ciudad, cod_pais, pais, ciudad, total_zonas, aud_usuario " +
                "FROM p_list_ciudad(p_ciudad := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapCiudadResponse, ciudad, "L");
    }

    /**
     * Verificar si existe una ciudad
     */
    public boolean existeCiudad(int codCiudad) {
        return buscarPorIdCompleto(codCiudad).isPresent();
    }

    /**
     * Buscar ciudad por ID (solo para validaciones)
     */
    public Optional<Ciudad> buscarPorId(int codCiudad) {
        String sql = "SELECT cod_ciudad, cod_pais, pais, ciudad, total_zonas, aud_usuario " +
                "FROM p_list_ciudad(p_codciudad := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapCiudad, codCiudad, "L");
    }

    /**
     * Crear nueva ciudad
     */
    public int crearCiudad(Ciudad ciudad) {
        String sql = "SELECT p_abm_ciudad(" +
                "p_codpais := ?, " +
                "p_ciudad := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                ciudad.getCodPais(),
                ciudad.getCiudad(),
                ciudad.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al crear ciudad"));
    }

    /**
     * Actualizar ciudad
     */
    public void actualizarCiudad(Ciudad ciudad) {
        String sql = "SELECT p_abm_ciudad(" +
                "p_codciudad := ?, " +
                "p_codpais := ?, " +
                "p_ciudad := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                ciudad.getCodCiudad(),
                ciudad.getCodPais(),
                ciudad.getCiudad(),
                ciudad.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar ciudad"));
    }

    /**
     * Eliminar ciudad
     */
    public void eliminarCiudad(int codCiudad, int audUsuario) {
        String sql = "SELECT p_abm_ciudad(" +
                "p_codciudad := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                codCiudad,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar ciudad"));
    }

    /**
     * Mapear ResultSet a CiudadResponse
     */
    private CiudadResponse mapCiudadResponse(ResultSet rs) throws SQLException {
        return CiudadResponse.builder()
                .codCiudad(rs.getInt(1))
                .codPais(rs.getInt(2))
                .pais(rs.getString(3))
                .ciudad(rs.getString(4))
                .totalZonas(rs.getInt(5))
                .audUsuario(rs.getInt(6))
                .build();
    }

    /**
     * Mapear ResultSet a Ciudad
     */
    private Ciudad mapCiudad(ResultSet rs) throws SQLException {
        return Ciudad.builder()
                .codCiudad(rs.getInt(1))
                .codPais(rs.getInt(2))
                .ciudad(rs.getString(3))
                .audUsuario(rs.getInt(4))
                .build();
    }
}