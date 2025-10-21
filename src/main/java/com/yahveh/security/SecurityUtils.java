package com.yahveh.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
public class SecurityUtils {

    /**
     * Obtener el ID del usuario actual desde el JWT
     */
    public int getCurrentUserId(JsonWebToken jwt) {
        Object claim = jwt.getClaim("codUsuario");
        if (claim == null) {
            throw new IllegalStateException("codUsuario no encontrado en el token");
        }

        // Manejar diferentes tipos de retorno
        if (claim instanceof Long) {
            return (int) claim;
        } else if (claim instanceof Number) {
            return ((Number) claim).intValue();
        } else {
            return Integer.parseInt(claim.toString());
        }
    }

    /**
     * Obtener el login del usuario actual
     */
    public String getCurrentUserLogin(JsonWebToken jwt) {
        return jwt.getName();
    }

    /**
     * Obtener el tipo de usuario actual
     */
    public String getCurrentUserType(JsonWebToken jwt) {
        Object claim = jwt.getClaim("tipoUsuario");
        return claim != null ? claim.toString() : null;
    }

    /**
     * Verificar si el usuario tiene un rol específico
     */
    public boolean hasRole(JsonWebToken jwt, String role) {
        return jwt.getGroups().contains(role);
    }
}