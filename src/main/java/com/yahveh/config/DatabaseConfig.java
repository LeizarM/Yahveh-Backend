package com.yahveh.config;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Startup
@ApplicationScoped
public class DatabaseConfig {

    @Inject
    @SuppressWarnings("CdiInjectionPointsInspection")
    AgroalDataSource dataSource;

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @PostConstruct
    public void testConnection() {
        try (Connection conn = getConnection()) {
            log.info("Conexión a base de datos exitosa: {}", conn.getMetaData().getURL());
        } catch (SQLException e) {
            log.error("Error al conectar a la base de datos", e);
        }
    }
}