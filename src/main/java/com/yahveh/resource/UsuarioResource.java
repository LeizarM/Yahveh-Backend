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
    SecurityUtils securityUtils;

    @GET
    @RolesAllowed({"admin", "lim"})
    public Response listarUsuarios() {
        log.info("GET /api/usuarios - Usuario: {}", securityUtils.getCurrentUsername());

        List<UsuarioResponse> usuarios = usuarioService.listarTodos();
        return Response.ok(ApiResponse.success(usuarios)).build();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"admin", "lim"})
    public Response buscarUsuario(@PathParam("id") int id) {
        log.info("GET /api/usuarios/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        UsuarioResponse usuario = usuarioService.buscarPorId(id);
        return Response.ok(ApiResponse.success(usuario)).build();
    }

    @POST
    @RolesAllowed({"admin", "lim"})
    public Response crearUsuario(@Valid UsuarioRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("POST /api/usuarios - Usuario: {} - Creando: {}", securityUtils.getCurrentUsername(), request.getLogin());

        Long nuevoId = usuarioService.crearUsuario(request, codUsuario);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("Usuario creado exitosamente", nuevoId))
                .build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({"admin", "lim"})
    public Response actualizarUsuario(@PathParam("id") int id, @Valid UsuarioRequest request) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("PUT /api/usuarios/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        usuarioService.actualizarUsuario(id, request, codUsuario);
        return Response.ok(ApiResponse.success("Usuario actualizado exitosamente", null)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin", "lim"})
    public Response eliminarUsuario(@PathParam("id") int id) {
        int codUsuario = securityUtils.getCurrentUserId();
        log.info("DELETE /api/usuarios/{} - Usuario: {}", id, securityUtils.getCurrentUsername());

        usuarioService.eliminarUsuario(id, codUsuario);
        return Response.ok(ApiResponse.success("Usuario eliminado exitosamente", null)).build();
    }
}