package com.yahveh.repository;

import com.yahveh.dto.response.ZonaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Zona;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ZonaRepository extends BaseRepository<Zona> {

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
     * Crear nueva zona con manejo de errores
     */
    public int crearZona(Zona zona) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_zona(" +
                "p_codciudad := ?, " +
                "p_zona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                zona.getCodCiudad(),
                zona.getZona(),
                zona.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_zona"));

        if (!result.isSuccess()) {
            log.error("Error al crear zona. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Zona creada exitosamente con ID: {}", result.result);
        return result.result;
    }

    /**
     * Actualizar zona con manejo de errores
     */
    public void actualizarZona(Zona zona) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_zona(" +
                "p_codzona := ?, " +
                "p_codciudad := ?, " +
                "p_zona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                zona.getCodZona(),
                zona.getCodCiudad(),
                zona.getZona(),
                zona.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_zona"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar zona. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Zona actualizada exitosamente: {}", zona.getCodZona());
    }

    /**
     * Eliminar zona con manejo de errores
     */
    public void eliminarZona(int codZona, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_zona(" +
                "p_codzona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codZona,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_zona"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar zona. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Zona eliminada exitosamente: {}", codZona);
    }

    /**
     * Mapear ResultSet a ZonaResponse
     */
    private ZonaResponse mapZonaResponse(ResultSet rs) throws SQLException {
        return ZonaResponse.builder()
                .codZona(rs.getInt(1))
                .codCiudad(rs.getInt(2))
                .ciudad(rs.getString(3))
                .zona(rs.getString(4))
                .totalClientes(rs.getInt(5))
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
        result.result = rs.wasNull() ? null : rs.getInt("p_result");

        return result;
    }
}