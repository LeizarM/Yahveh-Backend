package com.yahveh.repository;

import com.yahveh.dto.response.FamiliaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Familia;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class FamiliaRepository extends BaseRepository<Familia> {

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
     * Crear nueva familia con manejo de errores
     */
    public int crearFamilia(Familia familia) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_familia(" +
                "p_familia := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                familia.getFamilia(),
                familia.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear familia. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar familia con manejo de errores
     */
    public void actualizarFamilia(Familia familia) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_familia(" +
                "p_codfamilia := ?, " +
                "p_familia := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                familia.getCodFamilia(),
                familia.getFamilia(),
                familia.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar familia. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar familia con manejo de errores
     */
    public void eliminarFamilia(int codFamilia, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_familia(" +
                "p_codfamilia := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codFamilia,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar familia. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
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