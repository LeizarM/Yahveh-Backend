package com.yahveh.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    public String generateToken(Long codUsuario, String login, String tipoUsuario) {
        // Normalizar el rol: el sistema solo maneja 'admin' y 'lim'.
        // Cualquier variante (mayúsculas, espacios) se normaliza y 'user' se mapea a 'lim'.
        String rol = tipoUsuario == null ? "" : tipoUsuario.trim().toLowerCase();
        if (rol.equals("user")) {
            rol = "lim";
        }

        Set<String> roles = new HashSet<>();
        roles.add(rol); // admin | lim

        String token = Jwt.issuer(issuer)
                .upn(login)
                .groups(roles)
                .claim("codUsuario", codUsuario)
                .claim("tipoUsuario", rol)
                .expiresIn(Duration.ofHours(8))
                .sign();

        log.info("Token generado para usuario: {} (rol: {})", login, rol);
        return token;
    }
}