package com.yahveh.repository;

import com.yahveh.dto.response.PaisResponse;
import com.yahveh.model.Pais;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PaisRepository extends BaseRepository<Pais> {

    /**
     * Listar todos los países con información completa
     */
    public List<PaisResponse> listarTodosCompleto() {
        String sql = "SELECT cod_pais, pais, total_ciudades, aud_usuario " +
                "FROM p_list_pais(p_accion := ?)";
        return executeQueryList(sql, this::mapPaisResponse, "L");
    }

    /**
     * Buscar país por ID con información completa
     */
    public Optional<PaisResponse> buscarPorIdCompleto(int codPais) {
        String sql = "SELECT cod_pais, pais, total_ciudades, aud_usuario " +
                "FROM p_list_pais(p_codpais := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapPaisResponse, codPais, "L");
    }

    /**
     * Buscar países por nombre con información completa
     */
    public List<PaisResponse> buscarPorNombreCompleto(String pais) {
        String sql = "SELECT cod_pais, pais, total_ciudades, aud_usuario " +
                "FROM p_list_pais(p_pais := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapPaisResponse, pais, "L");
    }

    /**
     * Verificar si existe un país
     */
    public boolean existePais(int codPais) {
        return buscarPorIdCompleto(codPais).isPresent();
    }

    /**
     * Buscar país por ID (solo para validaciones)
     */
    public Optional<Pais> buscarPorId(int codPais) {
        String sql = "SELECT cod_pais, pais, total_ciudades, aud_usuario " +
                "FROM p_list_pais(p_codpais := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapPais, codPais, "L");
    }

    /**
     * Crear nuevo país
     */
    public int crearPais(Pais pais) {
        String sql = "SELECT p_abm_pais(" +
                "p_pais := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                pais.getPais(),
                pais.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al crear país"));
    }

    /**
     * Actualizar país
     */
    public void actualizarPais(Pais pais) {
        String sql = "SELECT p_abm_pais(" +
                "p_codpais := ?, " +
                "p_pais := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                pais.getCodPais(),
                pais.getPais(),
                pais.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar país"));
    }

    /**
     * Eliminar país
     */
    public void eliminarPais(int codPais, int audUsuario) {
        String sql = "SELECT p_abm_pais(" +
                "p_codpais := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                codPais,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar país"));
    }

    /**
     * Mapear ResultSet a PaisResponse
     */
    private PaisResponse mapPaisResponse(ResultSet rs) throws SQLException {
        return PaisResponse.builder()
                .codPais(rs.getInt(1))
                .pais(rs.getString(2))
                .totalCiudades(rs.getInt(3))
                .audUsuario(rs.getInt(4))
                .build();
    }

    /**
     * Mapear ResultSet a Pais
     */
    private Pais mapPais(ResultSet rs) throws SQLException {
        return Pais.builder()
                .codPais(rs.getInt(1))
                .pais(rs.getString(2))
                .audUsuario(rs.getInt(3))
                .build();
    }
}