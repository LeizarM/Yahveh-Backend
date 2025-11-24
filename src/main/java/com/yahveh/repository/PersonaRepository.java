package com.yahveh.repository;

import com.yahveh.exception.BusinessException;
import com.yahveh.model.Persona;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PersonaRepository extends BaseRepository<Persona> {


    /**
     * Listar todas las personas
     */
    public List<Persona> listarTodas() {
        String sql = "SELECT * FROM p_list_persona(p_accion := 'L')";
        return executeQueryList(sql, this::mapPersona);
    }

    /**
     * Buscar persona por código
     */
    public Optional<Persona> buscarPorCodigo(Long codPersona) {
        String sql = "SELECT * FROM p_list_persona(p_codpersona := ?, p_accion := 'L')";
        return executeQuerySingle(sql, this::mapPersona, codPersona);
    }

    /**
     * Buscar persona por CI
     */
    public Optional<Persona> buscarPorCI(String ciNumero, String ciExpedido) {
        String sql = "SELECT * FROM p_list_persona(p_cinumero := ?, p_ciexpedido := ?, p_accion := 'B')";
        return executeQuerySingle(sql, this::mapPersona, ciNumero, ciExpedido);
    }

    /**
     * Buscar personas por nombre
     */
    public List<Persona> buscarPorNombre(String nombre) {
        String sql = "SELECT * FROM p_list_persona(p_nombres := ?, p_accion := 'L')";
        return executeQueryList(sql, this::mapPersona, nombre);
    }

    /**
     * Crear nueva persona
     */
    public long crearPersona(Persona persona) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_persona(" +
                "p_nombres := ?, " +
                "p_appaterno := ?, " +
                "p_apmaterno := ?, " +
                "p_cinumero := ?, " +
                "p_ciexpedido := ?, " +
                "p_cifechavencimiento := ?, " +
                "p_direccion := ?, " +
                "p_estadocivil := ?, " +
                "p_fechanacimiento := ?, " +
                "p_lugarnacimiento := ?, " +
                "p_sexo := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                persona.getNombres(),
                persona.getApPaterno(),
                persona.getApMaterno(),
                persona.getCiNumero(),
                persona.getCiExpedido(),
                persona.getCiFechaVencimiento() != null ? Date.valueOf(persona.getCiFechaVencimiento()) : null,
                persona.getDireccion(),
                persona.getEstadoCivil(),
                persona.getFechaNacimiento() != null ? Date.valueOf(persona.getFechaNacimiento()) : null,
                persona.getLugarNacimiento(),
                persona.getSexo(),
                persona.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear persona. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar persona
     */
    public void actualizarPersona(Persona persona) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_persona(" +
                "p_codpersona := ?, " +
                "p_nombres := ?, " +
                "p_appaterno := ?, " +
                "p_apmaterno := ?, " +
                "p_cinumero := ?, " +
                "p_ciexpedido := ?, " +
                "p_cifechavencimiento := ?, " +
                "p_direccion := ?, " +
                "p_estadocivil := ?, " +
                "p_fechanacimiento := ?, " +
                "p_lugarnacimiento := ?, " +
                "p_sexo := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                persona.getCodPersona(),
                persona.getNombres(),
                persona.getApPaterno(),
                persona.getApMaterno(),
                persona.getCiNumero(),
                persona.getCiExpedido(),
                persona.getCiFechaVencimiento() != null ? Date.valueOf(persona.getCiFechaVencimiento()) : null,
                persona.getDireccion(),
                persona.getEstadoCivil(),
                persona.getFechaNacimiento() != null ? Date.valueOf(persona.getFechaNacimiento()) : null,
                persona.getLugarNacimiento(),
                persona.getSexo(),
                persona.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar persona. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar persona
     */
    public void eliminarPersona(long codPersona, long audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_persona(" +
                "p_codpersona := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codPersona,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar persona. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Mapear ResultSet a Persona
     */
    private Persona mapPersona(ResultSet rs) throws SQLException {
        Persona persona = Persona.builder()
                .codPersona(rs.getLong(1))           // cod_persona
                .nombres(rs.getString(2))            // nombres
                .apPaterno(rs.getString(3))          // ap_paterno
                .apMaterno(rs.getString(4))          // ap_materno
                .ciNumero(rs.getString(5))           // ci_numero
                .ciExpedido(rs.getString(6))         // ci_expedido
                .direccion(rs.getString(8))          // direccion (posición 8)
                .estadoCivil(rs.getString(9))        // estado_civil (posición 9)
                .lugarNacimiento(rs.getString(11))   // lugar_nacimiento (posición 11)
                .sexo(rs.getString(12))              // sexo (posición 12)
                .audUsuario(rs.getLong(13))          // aud_usuario (posición 13)
                .build();

        // Manejar fechas que pueden ser null
        Date ciFechaVenc = rs.getDate(7);  // ci_fecha_vencimiento (posición 7)
        if (ciFechaVenc != null) {
            persona.setCiFechaVencimiento(ciFechaVenc.toLocalDate());
        }

        Date fechaNac = rs.getDate(10);  // fecha_nacimiento (posición 10)
        if (fechaNac != null) {
            persona.setFechaNacimiento(fechaNac.toLocalDate());
        }

        return persona;
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        long resultValue = rs.getLong("p_result");
        result.result = rs.wasNull() ? null : Integer.valueOf((int) resultValue);

        return result;
    }
}