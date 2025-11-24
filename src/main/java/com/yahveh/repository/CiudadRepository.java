package com.yahveh.repository;

import com.yahveh.dto.response.CiudadResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Ciudad;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class CiudadRepository extends BaseRepository<Ciudad> {

    /**

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
     * Crear nueva ciudad con manejo de errores
     */
    public int crearCiudad(Ciudad ciudad) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_ciudad(" +
                "p_codpais := ?, " +
                "p_ciudad := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                ciudad.getCodPais(),
                ciudad.getCiudad(),
                ciudad.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_ciudad"));

        if (!result.isSuccess()) {
            log.error("Error al crear ciudad. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Ciudad creada exitosamente con ID: {}", result.result);
        return result.result;
    }

    /**
     * Actualizar ciudad con manejo de errores
     */
    public void actualizarCiudad(Ciudad ciudad) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_ciudad(" +
                "p_codciudad := ?, " +
                "p_codpais := ?, " +
                "p_ciudad := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                ciudad.getCodCiudad(),
                ciudad.getCodPais(),
                ciudad.getCiudad(),
                ciudad.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_ciudad"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar ciudad. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Ciudad actualizada exitosamente: {}", ciudad.getCodCiudad());
    }

    /**
     * Eliminar ciudad con manejo de errores
     */
    public void eliminarCiudad(int codCiudad, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_ciudad(" +
                "p_codciudad := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codCiudad,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_ciudad"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar ciudad. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Ciudad eliminada exitosamente: {}", codCiudad);
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
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        // Manejar el caso donde p_result puede ser NULL
        int resultValue = rs.getInt("p_result");
        result.result = rs.wasNull() ? null : Integer.valueOf(resultValue);

        return result;
    }
}