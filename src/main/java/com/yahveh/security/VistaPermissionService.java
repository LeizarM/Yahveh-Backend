package com.yahveh.security;

import com.yahveh.model.Vista;
import com.yahveh.repository.VistaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Resuelve y cachea las vistas (pantallas) asignadas a cada usuario.
 * Cachea por usuario con un TTL corto para no golpear la BD en cada request.
 * El cambio de permisos por el admin invalida la entrada (ver VistaService).
 */
@ApplicationScoped
public class VistaPermissionService {

    private static final long TTL_MS = 60_000; // 60 segundos

    @Inject
    VistaRepository vistaRepository;

    private static final class Entry {
        final Set<String> vistas;
        final long ts;
        Entry(Set<String> vistas, long ts) {
            this.vistas = vistas;
            this.ts = ts;
        }
    }

    private final ConcurrentHashMap<Long, Entry> cache = new ConcurrentHashMap<>();

    /**
     * Direcciones de vista (normalizadas a minúsculas) asignadas al usuario.
     */
    public Set<String> getVistasDireccion(long codUsuario) {
        long now = System.currentTimeMillis();
        Entry e = cache.get(codUsuario);
        if (e != null && now - e.ts < TTL_MS) {
            return e.vistas;
        }
        Set<String> set = vistaRepository.listarTodas(codUsuario).stream()
                .map(Vista::getDireccion)
                .filter(d -> d != null && !d.isBlank())
                .map(d -> d.trim().toLowerCase())
                .collect(Collectors.toSet());
        cache.put(codUsuario, new Entry(set, now));
        return set;
    }

    /** Invalida la caché de un usuario (al cambiar sus permisos). */
    public void invalidar(long codUsuario) {
        cache.remove(codUsuario);
    }
}
