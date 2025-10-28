package com.yahveh.repository;

import com.yahveh.dto.response.FamiliaResponse;
import com.yahveh.model.Familia;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FamiliaRepository extends BaseRepository<Familia> {

    /**
     * Listar todas las familias con información completa
     */
    public List<FamiliaResponse> listarTodasCompleto() {
        String sql = "SELECT cod_familia, familia, total_lineas, aud_usuario " +
                "FROM p_list_familia(p_accion := ?)";
        return executeQueryList(sql, this::mapFamiliaResponse, "L");
    }

    /**
     * Buscar familia por ID con información completa
     */
    public Optional<FamiliaResponse> buscarPorIdCompleto(int codFamilia) {
        String sql = "SELECT cod_familia, familia, total_lineas, aud_usuario " +
                "FROM p_list_familia(p_codfamilia := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapFamiliaResponse, codFamilia, "L");
    }

    /**
     * Buscar familias por nombre con información completa
     */
    public List<FamiliaResponse> buscarPorNombreCompleto(String familia) {
        String sql = "SELECT cod_familia, familia, total_lineas, aud_usuario " +
                "FROM p_list_familia(p_familia := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapFamiliaResponse, familia, "L");
    }

    /**
     * Verificar si existe una familia
     */
    public boolean existeFamilia(int codFamilia) {
        return buscarPorIdCompleto(codFamilia).isPresent();
    }

    /**
     * Buscar familia por ID (solo para validaciones)
     */
    public Optional<Familia> buscarPorId(int codFamilia) {
        String sql = "SELECT cod_familia, familia, total_lineas, aud_usuario " +
                "FROM p_list_familia(p_codfamilia := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapFamilia, codFamilia, "L");
    }

    /**
     * Crear nueva familia
     */
    public int crearFamilia(Familia familia) {
        String sql = "SELECT p_abm_familia(" +
                "p_familia := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getInt(1),
                familia.getFamilia(),
                familia.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al crear familia"));
    }

    /**
     * Actualizar familia
     */
    public void actualizarFamilia(Familia familia) {
        String sql = "SELECT p_abm_familia(" +
                "p_codfamilia := ?, " +
                "p_familia := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                familia.getCodFamilia(),
                familia.getFamilia(),
                familia.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar familia"));
    }

    /**
     * Eliminar familia
     */
    public void eliminarFamilia(int codFamilia, int audUsuario) {
        String sql = "SELECT p_abm_familia(" +
                "p_codfamilia := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        executeQuerySingle(
                sql,
                rs -> rs.getLong(1),
                codFamilia,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar familia"));
    }

    /**
     * Mapear ResultSet a FamiliaResponse
     */
    private FamiliaResponse mapFamiliaResponse(ResultSet rs) throws SQLException {
        return FamiliaResponse.builder()
                .codFamilia(rs.getLong(1))
                .familia(rs.getString(2))
                .totalLineas(rs.getInt(3))
                .audUsuario(rs.getInt(4))
                .build();
    }

    /**
     * Mapear ResultSet a Familia
     */
    private Familia mapFamilia(ResultSet rs) throws SQLException {
        return Familia.builder()
                .codFamilia(rs.getLong(1))
                .familia(rs.getString(2))
                .audUsuario(rs.getInt(3))
                .build();
    }
}