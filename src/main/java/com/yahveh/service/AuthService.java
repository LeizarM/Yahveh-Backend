package com.yahveh.service;

import com.yahveh.dto.request.LoginRequest;
import com.yahveh.dto.response.LoginResponse;
import com.yahveh.exception.UnauthorizedException;
import com.yahveh.repository.UsuarioRepository;
import com.yahveh.security.JwtService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@ApplicationScoped
public class AuthService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getLogin());

        // Llamar al SP que valida con bcrypt
        var usuarioOpt = usuarioRepository.login(request.getLogin(), request.getPassword());

        if (usuarioOpt.isEmpty()) {
            log.warn("Credenciales inválidas para usuario: {}", request.getLogin());
            throw new UnauthorizedException("Credenciales inválidas");
        }

        Map<String, Object> usuario = usuarioOpt.get();

        // Verificar estado
        String estado = (String) usuario.get("estado");
        if (!"D".equals(estado)) {
            log.warn("Usuario inactivo: {}", request.getLogin());
            throw new UnauthorizedException("Usuario inactivo");
        }

        Long codUsuario = (Long) usuario.get("codUsuario");
        String login = (String) usuario.get("login");
        String tipoUsuario = (String) usuario.get("tipoUsuario");
        String nombreEmpleado = (String) usuario.get("nombreEmpleado");

        // Generar token JWT
        String token = jwtService.generateToken(codUsuario, login, tipoUsuario);

        log.info("Login exitoso para usuario: {} ({})", login, tipoUsuario);

        return LoginResponse.builder()
                .token(token)
                .tipoUsuario(tipoUsuario)
                .codUsuario(codUsuario)
                .nombreCompleto(nombreEmpleado)
                .build();
    }
}