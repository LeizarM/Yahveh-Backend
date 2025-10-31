package com.yahveh.security;

import io.smallrye.jwt.build.Jwt;

import java.time.Duration;
import java.util.Set;

/**
 * Clase utilitaria para generar tokens JWT manualmente (testing)
 */
public class TokenGenerator {

    public static void main(String[] args) {
        String token = Jwt.issuer("https://yahveh.com")
                .upn("admin")
                .groups(Set.of("admin", "lim"))
                .claim("codUsuario", 1L)
                .claim("tipoUsuario", "admin")
                .expiresIn(Duration.ofHours(8))
                .sign();

        System.out.println("Token generado:");
        System.out.println(token);
    }
}