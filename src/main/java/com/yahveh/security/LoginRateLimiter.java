package com.yahveh.security;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limitador simple de intentos de login en memoria (anti fuerza bruta).
 * Bloquea por usuario (login) tras MAX_ATTEMPTS intentos fallidos dentro de
 * una ventana de WINDOW_MS. No requiere dependencias externas.
 *
 * Para un sistema interno pequeño esto es suficiente. Si en el futuro hay
 * múltiples instancias del backend, conviene mover el contador a una caché
 * compartida (Redis) o a la base de datos.
 */
@ApplicationScoped
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 5 * 60 * 1000L; // 5 minutos

    private static class Attempt {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStart = System.currentTimeMillis();
    }

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    private String key(String login) {
        return login == null ? "" : login.trim().toLowerCase();
    }

    /**
     * @return true si el usuario está actualmente bloqueado por exceso de intentos.
     */
    public boolean isBlocked(String login) {
        Attempt a = attempts.get(key(login));
        if (a == null) {
            return false;
        }
        // Si la ventana expiró, se reinicia el contador.
        if (System.currentTimeMillis() - a.windowStart > WINDOW_MS) {
            a.count.set(0);
            a.windowStart = System.currentTimeMillis();
            return false;
        }
        return a.count.get() >= MAX_ATTEMPTS;
    }

    /**
     * Registra un intento fallido.
     */
    public void recordFailure(String login) {
        Attempt a = attempts.computeIfAbsent(key(login), k -> new Attempt());
        if (System.currentTimeMillis() - a.windowStart > WINDOW_MS) {
            a.count.set(0);
            a.windowStart = System.currentTimeMillis();
        }
        a.count.incrementAndGet();
    }

    /**
     * Login exitoso: limpia el contador del usuario.
     */
    public void recordSuccess(String login) {
        attempts.remove(key(login));
    }
}
