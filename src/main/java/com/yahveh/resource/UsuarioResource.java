package com.yahveh.resource;

import com.yahveh.dto.request.UsuarioRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.UsuarioResponse;
import com.yahveh.security.SecurityUtils;
import com.yahveh.service.UsuarioService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Slf4j
@Path("/api/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    JsonWebToken jwt;

    @Inject
    SecurityUtils securityUtils;

    @GET
    @RolesAllowed({"ADMIN", "admin"})
    public Response listarUsuarios() {
        log.info("GET /api/usuarios - Usuario: {}", jwt.getName());

        List<UsuarioResponse> usuarios = usuarioService.listarTodos();
        return Response.ok(ApiResponse.success(usuarios)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "admin"})
    public Response buscarUsuario(@PathParam("id") Long id) {
        log.info("GET /api/usuarios/{} - Usuario: {}", id, jwt.getName());

        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        return Response.ok(ApiResponse.success(usuario)).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "admin"})
    public Response crearUsuario(@Valid UsuarioRequest request) {
        Long codUsuario = securityUtils.getCurrentUserId(jwt);
        log.info("POST /api/usuarios - Usuario: {} - Creando: {}", jwt.getName(), request.getLogin());

        Long nuevoId = usuarioService.crearUsuario(request, codUsuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Usuario creado exitosamente", nuevoId))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "admin"})
    public Response actualizarUsuario(@PathParam("id") Long id, @Valid UsuarioRequest request) {
        Long codUsuario = securityUtils.getCurrentUserId(jwt);
        log.info("PUT /api/usuarios/{} - Usuario: {}", id, jwt.getName());

        usuarioService.actualizarUsuario(id, request, codUsuario);
        return Response.ok(ApiResponse.success("Usuario actualizado exitosamente", null)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "admin"})
    public Response eliminarUsuario(@PathParam("id") Long id) {
        Long codUsuario = securityUtils.getCurrentUserId(jwt);
        log.info("DELETE /api/usuarios/{} - Usuario: {}", id, jwt.getName());

        usuarioService.eliminarUsuario(id, codUsuario);
        return Response.ok(ApiResponse.success("Usuario eliminado exitosamente", null)).build();
    }
}