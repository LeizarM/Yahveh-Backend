package com.yahveh.repository;

import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class BaseRepository<T> {

    @Inject
    protected DataSource dataSource;

    protected <R> Optional<R> executeQuerySingle(String sql, ResultSetMapper<R> mapper, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error ejecutando query: {}", sql, e);
            throw new RuntimeException("Error en base de datos", e);
        }
        return Optional.empty();
    }

    protected <R> List<R> executeQueryList(String sql, ResultSetMapper<R> mapper, Object... params) {
        List<R> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error ejecutando query: {}", sql, e);
            throw new RuntimeException("Error en base de datos", e);
        }
        return results;
    }

    protected int executeUpdate(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setParameters(stmt, params);
            return stmt.executeUpdate();

        } catch (SQLException e) {
            log.error("Error ejecutando update: {}", sql, e);
            throw new RuntimeException("Error en base de datos", e);
        }
    }

    protected Long executeInsertReturningId(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(stmt, params);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error ejecutando insert: {}", sql, e);
            throw new RuntimeException("Error en base de datos", e);
        }
        return null;
    }

    private void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    @FunctionalInterface
    protected interface ResultSetMapper<R> {
        R map(ResultSet rs) throws SQLException;
    }
}