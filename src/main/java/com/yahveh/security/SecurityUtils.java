package com.yahveh.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

@RequestScoped
public class SecurityUtils {

    @Inject
    JsonWebToken jwt;

    // Cache para evitar parsing repetido de claims en la misma petición
    private Integer cachedUserId;
    private String cachedUserType;

    /**
     * Obtener el ID del usuario actual desde el JWT
     * Cached para evitar parsing repetido en la misma petición
     */
    public int getCurrentUserId() {
        if (cachedUserId != null) {
            return cachedUserId;
        }

        Object claim = jwt.getClaim("codUsuario");
        if (claim == null) {
            throw new IllegalStateException("codUsuario no encontrado en el token");
        }

        // Optimizado: Manejar diferentes tipos de retorno
        if (claim instanceof Number) {
            cachedUserId = ((Number) claim).intValue();
        } else {
            cachedUserId = Integer.parseInt(claim.toString());
        }
        
        return cachedUserId;
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
     * Cached para evitar parsing repetido en la misma petición
     */
    public String getCurrentUserType() {
        if (cachedUserType != null) {
            return cachedUserType;
        }
        
        Object claim = jwt.getClaim("tipoUsuario");
        cachedUserType = claim != null ? claim.toString() : null;
        return cachedUserType;
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