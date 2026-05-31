package com.yahveh.service;

import com.yahveh.dto.request.LoginRequest;
import com.yahveh.dto.response.ApiResponse;
import com.yahveh.dto.response.LoginResponse;
import com.yahveh.exception.UnauthorizedException;
import com.yahveh.repository.UsuarioRepository;
import com.yahveh.security.JwtService;
import com.yahveh.security.LoginRateLimiter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@ApplicationScoped
public class AuthService {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    JwtService jwtService;

    @Inject
    LoginRateLimiter rateLimiter;

    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getLogin());

        // Anti fuerza bruta: bloquear si se superó el límite de intentos fallidos.
        if (rateLimiter.isBlocked(request.getLogin())) {
            log.warn("Login bloqueado por exceso de intentos: {}", request.getLogin());
            throw new WebApplicationException(
                    Response.status(429) // Too Many Requests
                            .entity(ApiResponse.error("Demasiados intentos fallidos. Espere unos minutos e intente de nuevo."))
                            .build());
        }

        // Llamar al SP que valida con bcrypt
        var usuarioOpt = usuarioRepository.login(request.getLogin(), request.getPassword());

        if (usuarioOpt.isEmpty()) {
            rateLimiter.recordFailure(request.getLogin());
            log.warn("Credenciales inválidas para usuario: {}", request.getLogin());
            throw new UnauthorizedException("Credenciales inválidas");
        }

        Map<String, Object> usuario = usuarioOpt.get();

        // Credenciales correctas: limpiar el contador de intentos fallidos.
        rateLimiter.recordSuccess(request.getLogin());

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