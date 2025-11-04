package com.yahveh.repository;

import com.yahveh.dto.response.PaisResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Pais;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PaisRepository extends BaseRepository<Pais> {

    /**
     * Resultado de operación ABM
     */
    public static class AbmResult {
        public int error;
        public String errorMsg;
        public Integer result;

        public boolean isSuccess() {
            return error == 0;
        }
    }

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
     * Crear nuevo país con manejo de errores
     */
    public int crearPais(Pais pais) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_pais(" +
                "p_pais := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                pais.getPais(),
                pais.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_pais"));

        if (!result.isSuccess()) {
            log.error("Error al crear país. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("País creado exitosamente con ID: {}", result.result);
        return result.result;
    }

    /**
     * Actualizar país con manejo de errores
     */
    public void actualizarPais(Pais pais) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_pais(" +
                "p_codpais := ?, " +
                "p_pais := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                pais.getCodPais(),
                pais.getPais(),
                pais.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_pais"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar país. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("País actualizado exitosamente: {}", pais.getCodPais());
    }

    /**
     * Eliminar país con manejo de errores
     */
    public void eliminarPais(int codPais, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_pais(" +
                "p_codpais := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codPais,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_pais"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar país. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("País eliminado exitosamente: {}", codPais);
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