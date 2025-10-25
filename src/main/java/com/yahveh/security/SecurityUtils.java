package com.yahveh.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class SecurityUtils {

    @Inject
    JsonWebToken jwt;

    /**
     * Obtener el ID del usuario actual desde el JWT
     */
    public int getCurrentUserId() {
        Object claim = jwt.getClaim("codUsuario");
        if (claim == null) {
            throw new IllegalStateException("codUsuario no encontrado en el token");
        }

        // Manejar diferentes tipos de retorno
        if (claim instanceof Integer) {
            return (int) claim;
        } else if (claim instanceof Number) {
            return ((Number) claim).intValue();
        } else {
            return Integer.parseInt(claim.toString());
        }
    }

    /**
     * Obtener el username del usuario actual
     */
    public String getCurrentUsername() {
        return jwt.getName();
    }

    /**
     * Obtener el login del usuario actual (alias de getCurrentUsername)
     */
    public String getCurrentUserLogin() {
        return jwt.getName();
    }

    /**
     * Obtener el tipo de usuario actual
     */
    public String getCurrentUserType() {
        Object claim = jwt.getClaim("tipoUsuario");
        return claim != null ? claim.toString() : null;
    }

    /**
     * Verificar si el usuario tiene un rol específico
     */
    public boolean hasRole(String role) {
        return jwt.getGroups().contains(role);
    }

    /**
     * Obtener todos los roles del usuario
     */
    public java.util.Set<String> getRoles() {
        return jwt.getGroups();
    }

    /**
     * Verificar si el usuario es ADMIN
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Verificar si el usuario es USER
     */
    public boolean isUser() {
        return hasRole("USER");
    }
}