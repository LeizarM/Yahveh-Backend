package com.yahveh.service;

import com.yahveh.dto.request.EmpleadoRequest;
import com.yahveh.dto.response.EmpleadoResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Empleado;
import com.yahveh.repository.EmpleadoRepository;
import com.yahveh.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class EmpleadoService {

    @Inject
    EmpleadoRepository empleadoRepository;

    @Inject
    SecurityUtils securityUtils;

    public List<EmpleadoResponse> listar() {
        log.info("Listando todos los empleados");
        return empleadoRepository.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EmpleadoResponse buscarPorCodigo(long codEmpleado) {
        log.info("Buscando empleado: {}", codEmpleado);
        EmpleadoRepository.EmpleadoDetalle detalle = empleadoRepository.buscarPorCodigo(codEmpleado)
                .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));
        return toResponse(detalle);
    }

    public EmpleadoResponse buscarPorPersona(long codPersona) {
        log.info("Buscando empleado por persona: {}", codPersona);
        EmpleadoRepository.EmpleadoDetalle detalle = empleadoRepository.buscarPorPersona(codPersona)
                .orElseThrow(() -> new NotFoundException("Empleado no encontrado"));
        return toResponse(detalle);
    }

    public List<EmpleadoResponse> buscarPorNombre(String nombre) {
        log.info("Buscando empleados por nombre: {}", nombre);
        return empleadoRepository.buscarPorNombre(nombre).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EmpleadoResponse crear(EmpleadoRequest request) {
        log.info("Creando empleado para persona: {}", request.getCodPersona());

        validarRequest(request);

        long audUsuario = securityUtils.getCurrentUserId();

        Empleado empleado = Empleado.builder()
                .codPersona(request.getCodPersona())
                .audUsuario(audUsuario)
                .build();

        Long codEmpleado = empleadoRepository.crearEmpleado(empleado);

        return buscarPorCodigo(codEmpleado);
    }

    public EmpleadoResponse actualizar(long codEmpleado, EmpleadoRequest request) {
        log.info("Actualizando empleado: {}", codEmpleado);

        // Validar que existe
        buscarPorCodigo(codEmpleado);

        validarRequest(request);

        long audUsuario = securityUtils.getCurrentUserId();

        Empleado empleado = Empleado.builder()
                .codEmpleado(codEmpleado)
                .codPersona(request.getCodPersona())
                .audUsuario(audUsuario)
                .build();

        empleadoRepository.actualizarEmpleado(empleado);

        return buscarPorCodigo(codEmpleado);
    }

    public void eliminar(long codEmpleado) {
        log.info("Eliminando empleado: {}", codEmpleado);

        // Validar que existe
        buscarPorCodigo(codEmpleado);

        long audUsuario = securityUtils.getCurrentUserId();

        empleadoRepository.eliminarEmpleado(codEmpleado, audUsuario);
    }

    private void validarRequest(EmpleadoRequest request) {
        if (request.getCodPersona() <= 0) {
            throw new BusinessException("El código de persona es obligatorio");
        }
    }

    private EmpleadoResponse toResponse(EmpleadoRepository.EmpleadoDetalle detalle) {
        EmpleadoResponse response = EmpleadoResponse.builder()
                .codEmpleado(detalle.codEmpleado)
                .codPersona(detalle.codPersona)
                .nombres(detalle.nombres)
                .apPaterno(detalle.apPaterno)
                .apMaterno(detalle.apMaterno)
                .nombreCompleto(detalle.nombreCompleto)
                .ciNumero(detalle.ciNumero)
                .ciExpedido(detalle.ciExpedido)
                .sexo(detalle.sexo)
                .audUsuario(detalle.audUsuario)
                .build();

        // CI completo
        response.setCiCompleto(detalle.ciNumero + " " + detalle.ciExpedido);

        // Descripción del sexo
        response.setSexoDescripcion(getSexoDescripcion(detalle.sexo));

        return response;
    }

    private String getSexoDescripcion(String sexo) {
        if (sexo == null) return "No especificado";
        return switch (sexo) {
            case "M" -> "Masculino";
            case "F" -> "Femenino";
            default -> "No especificado";
        };
    }
}