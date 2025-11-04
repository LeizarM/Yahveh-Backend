package com.yahveh.service;

import com.yahveh.dto.request.UsuarioRequest;
import com.yahveh.dto.response.UsuarioResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Usuario;
import com.yahveh.repository.UsuarioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class UsuarioService {

    @Inject
    UsuarioRepository usuarioRepository;

    /**
     * Listar todos los usuarios
     */
    public List<UsuarioResponse> listarTodos() {
        log.info("Listando todos los usuarios");
        return usuarioRepository.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Buscar usuario por ID
     */
    public UsuarioResponse buscarPorId(int codUsuario) {
        log.info("Buscando usuario con ID: {}", codUsuario);
        return usuarioRepository.buscarPorId(codUsuario)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));
    }

    /**
     * Crear nuevo usuario
     */
    public int crearUsuario(UsuarioRequest request, int audUsuario) {
        log.info("Creando nuevo usuario: {}", request.getLogin());

        Usuario usuario = Usuario.builder()
                .codEmpleado(request.getCodEmpleado())
                .login(request.getLogin())
                .tipoUsuario(request.getTipoUsuario())
                .estado(request.getEstado() != null ? request.getEstado() : "A")
                .audUsuario(audUsuario)
                .build();

        int codUsuario = usuarioRepository.crearUsuario(usuario, request.getPassword());

        log.info("Usuario creado exitosamente con ID: {}", codUsuario);
        return codUsuario;
    }

    /**
     * Actualizar usuario existente
     */
    public void actualizarUsuario(int codUsuario, UsuarioRequest request, int audUsuario) {
        log.info("Actualizando usuario ID: {}", codUsuario);

        // Verificar que existe
        usuarioRepository.buscarPorId(codUsuario)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Usuario usuario = Usuario.builder()
                .codUsuario(codUsuario)
                .codEmpleado(request.getCodEmpleado())
                .login(request.getLogin())
                .tipoUsuario(request.getTipoUsuario())
                .estado(request.getEstado())
                .audUsuario(audUsuario)
                .build();

        // Si se proporciona password, actualizar con password
        if (request.cambiaPassword()) {
            log.info("Actualizando usuario con cambio de password");
            usuarioRepository.actualizarUsuarioConPassword(usuario, request.getPassword());
        } else {
            log.info("Actualizando usuario sin cambiar password");
            usuarioRepository.actualizarUsuario(usuario);
        }

        log.info("Usuario actualizado exitosamente");
    }

    /**
     * Eliminar usuario
     */
    public void eliminarUsuario(int codUsuario, int audUsuario) {
        log.info("Eliminando usuario ID: {}", codUsuario);

        // Verificar que existe
        usuarioRepository.buscarPorId(codUsuario)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        usuarioRepository.eliminarUsuario(codUsuario, audUsuario);

        log.info("Usuario eliminado exitosamente");
    }

    /**
     * Convertir Usuario a UsuarioResponse
     */
    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .codUsuario(usuario.getCodUsuario())
                .codEmpleado(usuario.getCodEmpleado())
                .login(usuario.getLogin())
                .tipoUsuario(usuario.getTipoUsuario())
                .estado(usuario.getEstado())
                .build();
    }
}