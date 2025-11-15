package com.yahveh.repository;

import com.yahveh.dto.response.PrecioArticuloResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.PrecioArticulo;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class PrecioArticuloRepository extends BaseRepository<PrecioArticulo> {

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
     * Listar todos los precios con información completa
     */
    public List<PrecioArticuloResponse> listarTodosCompleto() {
        String sql = "SELECT cod_precio, cod_articulo, descripcion_articulo, linea, " +
                "lista_precio, precio_base, precio, precio_sin_factura, fecha_registro " +
                "FROM p_list_precio_articulo(p_accion := ?)";
        return executeQueryList(sql, this::mapPrecioArticuloResponse, "L");
    }

    /**
     * Buscar precio por ID con información completa
     */
    public Optional<PrecioArticuloResponse> buscarPorIdCompleto(int codPrecio) {
        String sql = "SELECT cod_precio, cod_articulo, descripcion_articulo, linea, " +
                "lista_precio, precio_base, precio, precio_sin_factura, fecha_registro " +
                "FROM p_list_precio_articulo(p_codprecio := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapPrecioArticuloResponse, codPrecio, "L");
    }

    /**
     * Listar precios por artículo
     */
    public List<PrecioArticuloResponse> listarPorArticulo(String codArticulo) {
        String sql = "SELECT cod_precio, cod_articulo, descripcion_articulo, linea, " +
                "lista_precio, precio_base, precio, precio_sin_factura, fecha_registro " +
                "FROM p_list_precio_articulo(p_codarticulo := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapPrecioArticuloResponse, codArticulo, "A");
    }

    /**
     * Crear nuevo precio con manejo de errores
     */
    public int crearPrecio(PrecioArticulo precio) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_precio_articulo(" +
                "p_codarticulo := ?, " +
                "p_listaprecio := ?, " +
                "p_preciobase := ?, " +
                "p_precio := ?, " +
                "p_preciosinfactura := ?, " +
                "p_audusuario := ?, " +
                "p_accion := ?)";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                precio.getCodArticulo(),
                precio.getListaPrecio(),
                precio.getPrecioBase(),
                precio.getPrecio(),
                precio.getPrecioSinFactura(),
                precio.getAudUsuario(),
                "I"
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_precio_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al crear precio. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Precio creado exitosamente con ID: {}", result.result);
        return result.result;
    }

    /**
     * Actualizar precio con manejo de errores
     */
    public void actualizarPrecio(PrecioArticulo precio) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_precio_articulo(" +
                "p_codprecio := ?, " +
                "p_codarticulo := ?, " +
                "p_listaprecio := ?, " +
                "p_preciobase := ?, " +
                "p_precio := ?, " +
                "p_preciosinfactura := ?, " +
                "p_audusuario := ?, " +
                "p_accion := ?)";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                precio.getCodPrecio(),
                precio.getCodArticulo(),
                precio.getListaPrecio(),
                precio.getPrecioBase(),
                precio.getPrecio(),
                precio.getPrecioSinFactura(),
                precio.getAudUsuario(),
                "U"
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_precio_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar precio. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Precio actualizado exitosamente: {}", precio.getCodPrecio());
    }

    /**
     * Merge (UPSERT) precio - Actualiza si existe, inserta si no existe
     * Basado en codArticulo + listaPrecio
     */
    public int mergePrecio(PrecioArticulo precio) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_precio_articulo(" +
                "p_codarticulo := ?, " +
                "p_listaprecio := ?, " +
                "p_preciobase := ?, " +
                "p_precio := ?, " +
                "p_preciosinfactura := ?, " +
                "p_audusuario := ?, " +
                "p_accion := ?)";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                precio.getCodArticulo(),
                precio.getListaPrecio(),
                precio.getPrecioBase(),
                precio.getPrecio(),
                precio.getPrecioSinFactura(),
                precio.getAudUsuario(),
                "M"
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_precio_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al hacer merge de precio. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Precio procesado exitosamente (merge) con ID: {}", result.result);
        return result.result;
    }

    /**
     * Eliminar precio con manejo de errores
     */
    public void eliminarPrecio(int codPrecio, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_precio_articulo(" +
                "p_codprecio := ?, " +
                "p_audusuario := ?, " +
                "p_accion := ?)";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codPrecio,
                audUsuario,
                "D"
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento p_abm_precio_articulo"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar precio. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        log.info("Precio eliminado exitosamente: {}", codPrecio);
    }

    /**
     * Mapear ResultSet a PrecioArticuloResponse
     */
    private PrecioArticuloResponse mapPrecioArticuloResponse(ResultSet rs) throws SQLException {
        return PrecioArticuloResponse.builder()
                .codPrecio(rs.getInt(1))
                .codArticulo(rs.getString(2))
                .descripcionArticulo(rs.getString(3))
                .linea(rs.getString(4))
                .listaPrecio(rs.getInt(5))
                .precioBase(rs.getFloat(6))
                .precio(rs.getFloat(7))
                .precioSinFactura(rs.getFloat(8))
                //.fechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime())
                .build();
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        int resultValue = rs.getInt("p_result");
        result.result = rs.wasNull() ? null : resultValue;

        return result;
    }
}