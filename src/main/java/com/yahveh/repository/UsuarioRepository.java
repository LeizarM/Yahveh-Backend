package com.yahveh.repository;

import com.yahveh.model.Usuario;
import com.yahveh.repository.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class UsuarioRepository extends BaseRepository<Usuario> {

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
    public Optional<Usuario> buscarPorId(Long codUsuario) {
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
     * Listar usuarios con filtros
     */
    public List<Usuario> listarConFiltros(Long codEmpleado, String login, String tipoUsuario, String estado) {
        String sql = "SELECT * FROM p_list_usuario(NULL, ?, ?, NULL, ?, ?, NULL, 'L')";

        return executeQueryList(sql, this::mapUsuario, codEmpleado, login, tipoUsuario, estado);
    }

    /**
     * Crear nuevo usuario
     */
    public Long crearUsuario(Usuario usuario, String passwordPlain) {
        String sql = "SELECT p_abm_usuario(NULL, ?, ?, ?, ?, ?, ?, 'I')";

        return executeQuerySingle(sql, rs -> rs.getLong(1),
                usuario.getCodEmpleado(),
                usuario.getLogin(),
                passwordPlain,
                usuario.getTipoUsuario(),
                usuario.getEstado(),
                usuario.getAudUsuario()
        ).orElse(null);
    }

    /**
     * Actualizar usuario (sin cambiar password)
     */
    public Long actualizarUsuario(Usuario usuario) {
        String sql = "SELECT p_abm_usuario(?, ?, ?, NULL, ?, ?, ?, 'U')";

        return executeQuerySingle(sql, rs -> rs.getLong(1),
                usuario.getCodUsuario(),
                usuario.getCodEmpleado(),
                usuario.getLogin(),
                usuario.getTipoUsuario(),
                usuario.getEstado(),
                usuario.getAudUsuario()
        ).orElse(null);
    }

    /**
     * Actualizar usuario con cambio de password
     */
    public Long actualizarUsuarioConPassword(Usuario usuario, String passwordPlain) {
        String sql = "SELECT p_abm_usuario(?, ?, ?, ?, ?, ?, ?, 'U')";

        return executeQuerySingle(sql, rs -> rs.getLong(1),
                usuario.getCodUsuario(),
                usuario.getCodEmpleado(),
                usuario.getLogin(),
                passwordPlain,
                usuario.getTipoUsuario(),
                usuario.getEstado(),
                usuario.getAudUsuario()
        ).orElse(null);
    }

    /**
     * Eliminar usuario (soft delete)
     */
    public Long eliminarUsuario(Long codUsuario, Long audUsuario) {
        String sql = "SELECT p_abm_usuario(?, NULL, NULL, NULL, NULL, NULL, ?, 'D')";

        return executeQuerySingle(sql, rs -> rs.getLong(1),
                codUsuario,
                audUsuario
        ).orElse(null);
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
                .audUsuario(rs.getLong("aud_usuario"))
                //.audFecha(getLocalDateTime(rs, "aud_fecha"))
                .build();
    }

    /**
     * Helper para convertir Timestamp a LocalDateTime
     */
    private LocalDateTime getLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }
}