package com.yahveh.service;

import com.yahveh.dto.request.TelefonoClienteRequest;
import com.yahveh.dto.response.TelefonoClienteResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.TelefonoCliente;
import com.yahveh.repository.TelefonoClienteRepository;
import com.yahveh.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class TelefonoClienteService {

    @Inject
    TelefonoClienteRepository telefonoClienteRepository;

    @Inject
    SecurityUtils securityUtils;

    public List<TelefonoClienteResponse> listar() {
        log.info("Listando todos los teléfonos de clientes");
        return telefonoClienteRepository.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TelefonoClienteResponse buscarPorCodigo(Long codTlfCliente) {
        log.info("Buscando teléfono: {}", codTlfCliente);
        TelefonoClienteRepository.TelefonoClienteDetalle detalle =
                telefonoClienteRepository.buscarPorCodigo(codTlfCliente)
                        .orElseThrow(() -> new NotFoundException("Teléfono no encontrado"));
        return toResponse(detalle);
    }

    public List<TelefonoClienteResponse> listarPorCliente(Long codCliente) {
        log.info("Listando teléfonos del cliente: {}", codCliente);
        return telefonoClienteRepository.listarPorCliente(codCliente).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TelefonoClienteResponse crear(TelefonoClienteRequest request) {
        log.info("Creando teléfono para cliente: {}", request.getCodCliente());

        validarRequest(request);

        int audUsuario = securityUtils.getCurrentUserId();

        TelefonoCliente telefono = TelefonoCliente.builder()
                .codCliente(request.getCodCliente())
                .telefono(request.getTelefono())
                .audUsuario(audUsuario)
                .build();

        long codTlfCliente = telefonoClienteRepository.crearTelefono(telefono);

        return buscarPorCodigo(codTlfCliente);
    }

    public TelefonoClienteResponse actualizar(long codTlfCliente, TelefonoClienteRequest request) {
        log.info("Actualizando teléfono: {}", codTlfCliente);

        // Validar que existe
        buscarPorCodigo(codTlfCliente);

        validarRequest(request);

        int audUsuario = securityUtils.getCurrentUserId();

        TelefonoCliente telefono = TelefonoCliente.builder()
                .codTlfCliente(codTlfCliente)
                .codCliente(request.getCodCliente())
                .telefono(request.getTelefono())
                .audUsuario(audUsuario)
                .build();

        telefonoClienteRepository.actualizarTelefono(telefono);

        return buscarPorCodigo(codTlfCliente);
    }

    public void eliminar(long codTlfCliente) {
        log.info("Eliminando teléfono: {}", codTlfCliente);

        // Validar que existe
        buscarPorCodigo(codTlfCliente);

        long audUsuario = securityUtils.getCurrentUserId();

        telefonoClienteRepository.eliminarTelefono(codTlfCliente, audUsuario);
    }

    private void validarRequest(TelefonoClienteRequest request) {
        if ( request.getCodCliente() <= 0) {
            throw new BusinessException("El código de cliente es obligatorio");
        }

        if (request.getTelefono() == null || request.getTelefono().trim().isEmpty()) {
            throw new BusinessException("El teléfono es obligatorio");
        }

        // Validar formato de teléfono (opcional, ajusta según tus necesidades)
        String telefono = request.getTelefono().trim();
        if (telefono.length() < 7 || telefono.length() > 20) {
            throw new BusinessException("El teléfono debe tener entre 7 y 20 caracteres");
        }

        // Validar que solo contenga números, espacios, guiones, paréntesis y el símbolo +
        if (!telefono.matches("^[0-9\\s\\-\\(\\)\\+]+$")) {
            throw new BusinessException("El teléfono contiene caracteres no válidos");
        }
    }

    private TelefonoClienteResponse toResponse(TelefonoClienteRepository.TelefonoClienteDetalle detalle) {
        return TelefonoClienteResponse.builder()
                .codTlfCliente(detalle.codTlfCliente)
                .codCliente(detalle.codCliente)
                .telefono(detalle.telefono)
                .nombreCliente(detalle.nombreCliente)
                .audUsuario(detalle.audUsuario)
                .build();
    }
}