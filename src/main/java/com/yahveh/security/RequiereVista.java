package com.yahveh.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un recurso/método como protegido por permisos de VISTA (pantalla),
 * además del rol (@RolesAllowed). Un usuario 'lim' solo puede acceder si tiene
 * asignada (en tb_usuario_vista) al menos UNA de las vistas listadas.
 * El 'admin' siempre pasa (tiene todas las vistas).
 *
 * Los valores corresponden a tb_vista.direccion (ej: "nota_entrega", "reportes").
 *
 * Ej: @RequiereVista({"clientes", "nota_entrega"})
 *     → accesible si el usuario tiene la vista 'clientes' O 'nota_entrega'.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiereVista {
    String[] value();
}
