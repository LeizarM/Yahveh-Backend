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
        Set<String> roles = new HashSet<>();
        roles.add(tipoUsuario); // ADMIN, VENDEDOR, etc.

        String token = Jwt.issuer(issuer)
                .upn(login)
                .groups(roles)
                .claim("codUsuario", codUsuario)
                .claim("tipoUsuario", tipoUsuario)
                .expiresIn(Duration.ofHours(8))
                .sign();

        log.info("Token generado para usuario: {} ({})", login, tipoUsuario);
        return token;
    }
}