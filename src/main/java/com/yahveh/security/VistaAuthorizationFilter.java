package com.yahveh.security;

import com.yahveh.dto.response.ApiResponse;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Filtro de autorización por VISTA (pantalla). Complementa a @RolesAllowed:
 * después de validar el rol, si el endpoint tiene @RequiereVista, exige que el
 * usuario 'lim' tenga asignada al menos una de esas vistas en tb_usuario_vista.
 * El 'admin' siempre pasa.
 */
@Provider
@Priority(Priorities.AUTHORIZATION + 10) // corre después de la autorización por rol
@Slf4j
public class VistaAuthorizationFilter implements ContainerRequestFilter {

    @Context
    ResourceInfo resourceInfo;

    @Inject
    SecurityUtils securityUtils;

    @Inject
    VistaPermissionService vistaPermissionService;

    @Override
    public void filter(ContainerRequestContext ctx) {
        Method method = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();
        if (method == null) {
            return;
        }

        // Buscar @RequiereVista en el método; si no, en la clase
        RequiereVista ann = method.getAnnotation(RequiereVista.class);
        if (ann == null && resourceClass != null) {
            ann = resourceClass.getAnnotation(RequiereVista.class);
        }
        if (ann == null) {
            return; // endpoint sin requisito de vista
        }

        // Sin token válido → lo maneja la capa de autenticación/@RolesAllowed
        final int codUsuario;
        final Set<String> roles;
        try {
            roles = securityUtils.getRoles();
            codUsuario = securityUtils.getCurrentUserId();
        } catch (Exception e) {
            return;
        }
        if (roles == null || roles.isEmpty()) {
            return;
        }

        // Admin: acceso total
        if (securityUtils.isAdmin()) {
            return;
        }

        Set<String> permitidas = vistaPermissionService.getVistasDireccion(codUsuario);
        for (String requerida : ann.value()) {
            if (requerida != null && permitidas.contains(requerida.trim().toLowerCase())) {
                return; // tiene al menos una vista válida → permitir
            }
        }

        log.warn("Acceso DENEGADO: usuario {} a {} (requiere vista: {})",
                codUsuario,
                resourceClass != null ? resourceClass.getSimpleName() : "?",
                String.join(" | ", ann.value()));

        ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
                .entity(ApiResponse.error("No tiene permiso para acceder a esta sección"))
                .build());
    }
}
