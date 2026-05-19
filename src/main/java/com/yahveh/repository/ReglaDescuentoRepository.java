package com.yahveh.repository;

import com.yahveh.dto.response.ReglaDescuentoResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.ReglaDescuento;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ReglaDescuentoRepository extends BaseRepository<ReglaDescuento> {

    public static class AbmResult {
        public int error;
        public String errorMsg;
        public Long result;

        public boolean isSuccess() { return error == 0; }
    }

    public List<ReglaDescuentoResponse> listarTodas() {
        String sql = "SELECT * FROM p_list_regla_descuento()";
        return executeQueryList(sql, this::mapResponse);
    }

    public Optional<ReglaDescuentoResponse> buscarPorArticulo(String codArticulo) {
        String sql = "SELECT * FROM p_list_regla_descuento(p_codarticulo := ?)";
        return executeQuerySingle(sql, this::mapResponse, codArticulo);
    }

    public Optional<ReglaDescuentoResponse> buscarPorCodigo(long codReglaDescuento) {
        String sql = "SELECT * FROM p_list_regla_descuento(p_codregladescuento := ?)";
        return executeQuerySingle(sql, this::mapResponse, codReglaDescuento);
    }

    public long crear(String codArticulo, int cantidadMinima, float descuento, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result FROM p_abm_regla_descuento(" +
                "p_codarticulo := ?, p_cantidadminima := ?, p_descuento := ?, " +
                "p_audusuario := ?, p_accion := 'I')";

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codArticulo, cantidadMinima, descuento, audUsuario)
                .orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear regla: {} - {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
        return result.result;
    }

    public void actualizar(long codReglaDescuento, int cantidadMinima, float descuento, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result FROM p_abm_regla_descuento(" +
                "p_codregladescuento := ?, p_cantidadminima := ?, p_descuento := ?, " +
                "p_audusuario := ?, p_accion := 'U')";

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codReglaDescuento, cantidadMinima, descuento, audUsuario)
                .orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar regla: {} - {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    public void eliminar(long codReglaDescuento, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result FROM p_abm_regla_descuento(" +
                "p_codregladescuento := ?, p_audusuario := ?, p_accion := 'D')";

        AbmResult result = executeQuerySingle(sql, this::mapAbmResult,
                codReglaDescuento, audUsuario)
                .orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar regla: {} - {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    private ReglaDescuentoResponse mapResponse(ResultSet rs) throws SQLException {
        ReglaDescuentoResponse r = new ReglaDescuentoResponse();
        r.setCodReglaDescuento(rs.getLong(1));
        r.setCodArticulo(rs.getString(2));
        r.setDescripcionArticulo(rs.getString(3));
        r.setCantidadMinima(rs.getInt(4));
        r.setDescuento(rs.getFloat(5));
        r.setAudUsuario(rs.getLong(6));
        return r;
    }

    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");
        long val = rs.getLong("p_result");
        result.result = rs.wasNull() ? null : val;
        return result;
    }
}
