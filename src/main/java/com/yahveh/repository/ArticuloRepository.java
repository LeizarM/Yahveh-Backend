package com.yahveh.repository;

import com.yahveh.dto.response.ArticuloResponse;
import com.yahveh.model.Articulo;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ArticuloRepository extends BaseRepository<Articulo> {

    /**
     * Listar todos los artículos con información completa
     */
    public List<ArticuloResponse> listarTodosCompleto() {
        String sql = "SELECT cod_articulo, cod_linea, linea, descripcion, descripcion2, " +
                "total_stock, precio_actual, aud_usuario " +
                "FROM p_list_articulo(p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, "L");
    }

    /**
     * Buscar artículo por código con información completa
     */
    public Optional<ArticuloResponse> buscarPorCodigoCompleto(String codArticulo) {
        String sql = "SELECT cod_articulo, cod_linea, linea, descripcion, descripcion2, " +
                "total_stock, precio_actual, aud_usuario " +
                "FROM p_list_articulo(p_codarticulo := ?, p_accion := ?)";
        return executeQuerySingle(sql, this::mapArticuloResponse, codArticulo, "L");
    }

    /**
     * Listar artículos por línea con información completa
     */
    public List<ArticuloResponse> listarPorLineaCompleto(int codLinea) {
        String sql = "SELECT cod_articulo, cod_linea, linea, descripcion, descripcion2, " +
                "total_stock, precio_actual, aud_usuario " +
                "FROM p_list_articulo(p_codlinea := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, codLinea, "L");
    }

    /**
     * Buscar artículos por descripción con información completa
     */
    public List<ArticuloResponse> buscarPorDescripcionCompleto(String descripcion) {
        String sql = "SELECT cod_articulo, cod_linea, linea, descripcion, descripcion2, " +
                "total_stock, precio_actual, aud_usuario " +
                "FROM p_list_articulo(p_descripcion := ?, p_accion := ?)";
        return executeQueryList(sql, this::mapArticuloResponse, descripcion, "L");
    }

    /**
     * Verificar si existe un artículo (solo para validaciones)
     */
    public boolean existeArticulo(String codArticulo) {
        return buscarPorCodigoCompleto(codArticulo).isPresent();
    }

    /**
     * Crear nuevo artículo
     */
    public String crearArticulo(Articulo articulo) {
        String sql = "SELECT p_abm_articulo(" +
                "p_codarticulo := ?, " +
                "p_codlinea := ?, " +
                "p_descripcion := ?, " +
                "p_descripcion2 := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'I')";

        return executeQuerySingle(
                sql,
                rs -> rs.getString(1),
                articulo.getCodArticulo(),
                articulo.getCodLinea(),
                articulo.getDescripcion(),
                articulo.getDescripcion2(),
                articulo.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al crear artículo"));
    }

    /**
     * Actualizar artículo
     */
    public void actualizarArticulo(Articulo articulo) {

        System.out.println(articulo.toString());

        String sql = "SELECT p_abm_articulo(" +
                "p_codarticulo := ?, " +
                "p_codlinea := ?, " +
                "p_descripcion := ?, " +
                "p_descripcion2 := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'U')";

        executeQuerySingle(
                sql,
                rs -> rs.getString(1),
                articulo.getCodArticulo(),
                articulo.getCodLinea(),
                articulo.getDescripcion(),
                articulo.getDescripcion2(),
                articulo.getAudUsuario()
        ).orElseThrow(() -> new RuntimeException("Error al actualizar artículo"));
    }

    /**
     * Eliminar artículo
     */
    public void eliminarArticulo(String codArticulo, Long audUsuario) {
        String sql = "SELECT p_abm_articulo(" +
                "p_codarticulo := ?, " +
                "p_audusuario := ?, " +
                "p_accion := 'D')";

        executeQuerySingle(
                sql,
                rs -> rs.getString(1),
                codArticulo,
                audUsuario
        ).orElseThrow(() -> new RuntimeException("Error al eliminar artículo"));
    }

    /**
     * Mapear ResultSet a ArticuloResponse (con todos los datos del SP)
     *
     * Columnas del SP:
     * 1. cod_articulo (VARCHAR)
     * 2. cod_linea (INTEGER)
     * 3. linea (VARCHAR)
     * 4. descripcion (VARCHAR)
     * 5. descripcion2 (VARCHAR)
     * 6. total_stock (INTEGER)
     * 7. precio_actual (NUMERIC/DOUBLE)
     * 8. aud_usuario (BIGINT)
     */
    private ArticuloResponse mapArticuloResponse(ResultSet rs) throws SQLException {
        return ArticuloResponse.builder()
                .codArticulo(rs.getString(1))
                .codLinea(rs.getInt(2))
                .linea(rs.getString(3))
                .descripcion(rs.getString(4))
                .descripcion2(rs.getString(5))
                .stockActual(rs.getInt(6))
                .precioActual(rs.getDouble(7))
                .audUsuario(rs.getInt(8))
                .build();
    }
}