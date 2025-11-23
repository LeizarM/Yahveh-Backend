package com.yahveh.repository;

import com.yahveh.exception.BusinessException;
import com.yahveh.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class UsuarioRepository extends BaseRepository<Usuario> {

    /**
     * Resultado de operación ABM
     */
    public static class AbmResult {
        public int error;
        public String errorMsg;
        public Long result;

        public boolean isSuccess() {
            return error == 0;
        }
    }

    /**
     * Login con autenticación bcrypt
     */
    public Optional<Map<String, Object>> login(String login, String password) {
        String sql = "SELECT * FROM p_list_usuario(NULL, NULL, ?, ?, NULL, NULL, NULL, 'A')";
        log.debug("Intentando login para usuario: {}", login);
        return executeQuerySingle(sql, this::mapLoginResult, login, password);
    }

    /**
     * Listar todos los usuarios
     */
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM p_list_usuario(NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'L')";
        return executeQueryList(sql, this::mapUsuario);
    }

    /**
     * Buscar usuario por ID
     */
    public Optional<Usuario> buscarPorId(int codUsuario) {
        String sql = "SELECT * FROM p_list_usuario(?, NULL, NULL, NULL, NULL, NULL, NULL, 'L')";
        return executeQuerySingle(sql, this::mapUsuario, codUsuario);
    }

    /**
     * Buscar usuario por login (sin password)
     */
    public Optional<Usuario> buscarPorLogin(String login) {
        String sql = "SELECT * FROM p_list_usuario(NULL, NULL, ?, NULL, NULL, NULL, NULL, 'B')";
        return executeQuerySingle(sql, this::mapUsuario, login);
    }

    /**
     * Crear nuevo usuario con manejo de errores
     */
    public int crearUsuario(Usuario usuario, String passwordPlain) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_usuario(" +
                "p_codempleado := ?, " +
                "p_login := ?, " +
                "p_password := ?, " +
                "p_tipousuario := ?, " +
                "p_estado := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                usuario.getCodEmpleado(),
                usuario.getLogin(),
                passwordPlain,
                usuario.getTipoUsuario(),
                usuario.getEstado(),
                usuario.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_usuario"));

        if (!result.isSuccess()) {
            log.error("Error al crear usuario. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Usuario creado exitosamente con ID: {}", result.result);
        return result.result.intValue();
    }

    /**
     * Actualizar usuario (sin cambiar password) con manejo de errores
     */
    public void actualizarUsuario(Usuario usuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_usuario(" +
                "p_codusuario := ?, " +
                "p_codempleado := ?, " +
                "p_login := ?, " +
                "p_password := NULL, " +
                "p_tipousuario := ?, " +
                "p_estado := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                usuario.getCodUsuario(),
                usuario.getCodEmpleado(),
                usuario.getLogin(),
                usuario.getTipoUsuario(),
                usuario.getEstado(),
                usuario.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_usuario"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar usuario. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Usuario actualizado exitosamente: {}", usuario.getCodUsuario());
    }

    /**
     * Actualizar usuario con cambio de password con manejo de errores
     */
    public void actualizarUsuarioConPassword(Usuario usuario, String passwordPlain) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_usuario(" +
                "p_codusuario := ?, " +
                "p_codempleado := ?, " +
                "p_login := ?, " +
                "p_password := ?, " +
                "p_tipousuario := ?, " +
                "p_estado := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                usuario.getCodUsuario(),
                usuario.getCodEmpleado(),
                usuario.getLogin(),
                passwordPlain,
                usuario.getTipoUsuario(),
                usuario.getEstado(),
                usuario.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_usuario"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar usuario con password. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Usuario actualizado exitosamente con cambio de password: {}", usuario.getCodUsuario());
    }

    /**
     * Eliminar usuario (soft delete) con manejo de errores
     */
    public void eliminarUsuario(int codUsuario, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_usuario(" +
                "p_codusuario := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codUsuario,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_usuario"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar usuario. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Usuario eliminado exitosamente: {}", codUsuario);
    }

    /**
     * Mapear resultado de login
     */
    private Map<String, Object> mapLoginResult(ResultSet rs) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        result.put("codUsuario", rs.getLong("cod_usuario"));
        result.put("codEmpleado", rs.getLong("cod_empleado"));
        result.put("nombreEmpleado", rs.getString("nombre_empleado"));
        result.put("login", rs.getString("login"));
        result.put("tipoUsuario", rs.getString("tipo_usuario"));
        result.put("estado", rs.getString("estado"));
        result.put("audUsuario", rs.getLong("aud_usuario"));
        result.put("audFecha", rs.getTimestamp("aud_fecha"));
        return result;
    }

    /**
     * Mapear resultado a objeto Usuario
     */
    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        return Usuario.builder()
                .codUsuario(rs.getLong("cod_usuario"))
                .codEmpleado(rs.getLong("cod_empleado"))
                .login(rs.getString("login"))
                .tipoUsuario(rs.getString("tipo_usuario"))
                .estado(rs.getString("estado"))
                .audUsuario(rs.getInt("aud_usuario"))
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
        long resultValue = rs.getLong("p_result");
        result.result = rs.wasNull() ? null : resultValue;

        return result;
    }
}