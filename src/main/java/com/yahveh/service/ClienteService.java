package com.yahveh.service;

import com.yahveh.dto.request.CrearClienteRequest;
import com.yahveh.dto.response.ClienteResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Cliente;
import com.yahveh.repository.ClienteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository clienteRepository;

    public List<ClienteResponse> listarTodos() {
        log.info("Listando todos los clientes");

        return clienteRepository.listarTodos().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ClienteResponse buscarPorId(Long codCliente) {
        log.info("Buscando cliente con ID: {}", codCliente);

        return clienteRepository.buscarPorId(codCliente)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }

    public Long crearCliente(CrearClienteRequest request, Long audUsuario) {
        log.info("Creando nuevo cliente: {}", request.getNombreCliente());

        Cliente cliente = Cliente.builder()
                .codZona(request.getCodZona())
                .nit(request.getNit())
                .razonSocial(request.getRazonSocial())
                .nombreCliente(request.getNombreCliente())
                .direccion(request.getDireccion())
                .referencia(request.getReferencia())
                .obs(request.getObs())
                .build();

        return clienteRepository.crearCliente(cliente, audUsuario);
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .codCliente(cliente.getCodCliente())
                .codZona(cliente.getCodZona())
                .nit(cliente.getNit())
                .razonSocial(cliente.getRazonSocial())
                .nombreCliente(cliente.getNombreCliente())
                .direccion(cliente.getDireccion())
                .referencia(cliente.getReferencia())
                .obs(cliente.getObs())
                .build();
    }
}