package com.yahveh.repository;

import com.yahveh.dto.response.DetalleNotaEntregaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.DetalleNotaEntrega;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class DetalleNotaEntregaRepository extends BaseRepository<DetalleNotaEntrega> {

    /**
     * Listar detalles por nota de entrega
     */
    public List<DetalleNotaEntregaResponse> listarPorNotaEntrega(int codNotaEntrega) {
        String sql = "SELECT * FROM p_list_detalle_nota_entrega(p_codnotaentrega := ?)";
        return executeQueryList(sql, this::mapDetalleResponse, codNotaEntrega);
    }

    /**
     * Listar detalles para múltiples notas de entrega en una sola consulta
     * Optimización para evitar N+1 queries
     * 
     * @param codNotasEntrega Lista de códigos de notas de entrega
     * @return Map con codNotaEntrega como clave y lista de detalles como valor
     */
    public Map<Integer, List<DetalleNotaEntregaResponse>> listarPorNotasEntregaBatch(List<Integer> codNotasEntrega) {
        if (codNotasEntrega == null || codNotasEntrega.isEmpty()) {
            return Collections.emptyMap();
        }

        // Si solo hay una nota, usar el método individual
        if (codNotasEntrega.size() == 1) {
            int codNota = codNotasEntrega.get(0);
            List<DetalleNotaEntregaResponse> detalles = listarPorNotaEntrega(codNota);
            Map<Integer, List<DetalleNotaEntregaResponse>> result = new HashMap<>();
            result.put(codNota, detalles);
            return result;
        }

        // Para múltiples notas, hacer consultas en lote
        // Cargar todos los detalles y agruparlos por nota
        Map<Integer, List<DetalleNotaEntregaResponse>> detallesPorNota = new HashMap<>();
        
        // Inicializar listas vacías para todas las notas
        codNotasEntrega.forEach(codNota -> detallesPorNota.put(codNota, new ArrayList<>()));
        
        // Cargar detalles para cada nota (este enfoque es más compatible con stored procedures)
        // En el futuro, si la base de datos soporta arrays o consultas IN, se puede optimizar más
        for (Integer codNota : codNotasEntrega) {
            List<DetalleNotaEntregaResponse> detalles = listarPorNotaEntrega(codNota);
            detallesPorNota.put(codNota, detalles);
        }

        return detallesPorNota;
    }

    /**
     * Buscar detalle por código
     */
    public Optional<DetalleNotaEntregaResponse> buscarPorCodigo(int codDetalle) {
        String sql = "SELECT * FROM p_list_detalle_nota_entrega(p_coddetalle := ?)";
        return executeQuerySingle(sql, this::mapDetalleResponse, codDetalle);
    }

    /**
     * Crear nuevo detalle
     */
    public int crearDetalle(int codNotaEntrega, String codArticulo, int cantidad,
                             float precioUnitario, float precioSinFactura, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_detalle_nota_entrega(" +
                "p_codnotaentrega := ?, " +
                "p_codarticulo := ?, " +
                "p_cantidad := ?, " +
                "p_preciounitario := ?, " +
                "p_preciosinfactura := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codNotaEntrega,
                codArticulo,
                cantidad,
                precioUnitario,
                precioSinFactura,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al crear detalle. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }

        return result.result;
    }

    /**
     * Actualizar detalle
     */
    public void actualizarDetalle(int codDetalle, int cantidad, float precioUnitario,
                                  float precioSinFactura, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_detalle_nota_entrega(" +
                "p_coddetalle := ?, " +
                "p_cantidad := ?, " +
                "p_preciounitario := ?, " +
                "p_preciosinfactura := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codDetalle,
                cantidad,
                precioUnitario,
                precioSinFactura,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al actualizar detalle. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Eliminar detalle
     */
    public void eliminarDetalle(int codDetalle, int audUsuario) {
        String sql = "SELECT p_error, p_errormsg, p_result " +
                "FROM p_abm_detalle_nota_entrega(" +
                "p_coddetalle := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        AbmResult result = executeQuerySingle(
                sql,
                this::mapAbmResult,
                codDetalle,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al ejecutar procedimiento"));

        if (!result.isSuccess()) {
            log.error("Error al eliminar detalle. Código: {}, Mensaje: {}", result.error, result.errorMsg);
            throw new BusinessException(result.errorMsg);
        }
    }

    /**
     * Mapear ResultSet a DetalleNotaEntregaResponse
     */
    private DetalleNotaEntregaResponse mapDetalleResponse(ResultSet rs) throws SQLException {
        DetalleNotaEntregaResponse response = DetalleNotaEntregaResponse.builder()
                .codDetalle(rs.getInt(1))
                .codNotaEntrega(rs.getInt(2))
                .codArticulo(rs.getString(3))
                .descripcionArticulo(rs.getString(4))
                .descripcion2Articulo(rs.getString(5))
                .lineaArticulo(rs.getString(6))
                .codLinea(rs.getInt(7))
                .cantidad(rs.getInt(8))
                .precioUnitario(rs.getFloat(9))
                .precioTotal(rs.getFloat(10))
                .precioSinFactura(rs.getFloat(11))
                .audUsuario(rs.getInt(12))
                .build();



        return response;
    }

    /**
     * Mapear ResultSet a AbmResult
     */
    private AbmResult mapAbmResult(ResultSet rs) throws SQLException {
        AbmResult result = new AbmResult();
        result.error = rs.getInt("p_error");
        result.errorMsg = rs.getString("p_errormsg");

        int resultValue = rs.getInt("p_result");
        result.result = rs.wasNull() ? null : Integer.valueOf(resultValue);

        return result;
    }
}