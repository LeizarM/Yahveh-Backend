package com.yahveh.service;

import com.yahveh.dto.request.ClienteRequest;
import com.yahveh.dto.response.ClienteResponse;
import com.yahveh.exception.NotFoundException;
import com.yahveh.model.Cliente;
import com.yahveh.repository.ClienteRepository;
import com.yahveh.repository.ZonaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ZonaRepository zonaRepository;

    /**
     * Listar todos los clientes
     */
    public List<ClienteResponse> listarTodos() {
        log.info("Listando todos los clientes");
        return clienteRepository.listarTodosCompleto();
    }

    /**
     * Buscar cliente por ID
     */
    public ClienteResponse buscarPorId(int codCliente) {
        log.info("Buscando cliente con ID: {}", codCliente);
        return clienteRepository.buscarPorIdCompleto(codCliente)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }

    /**
     * Listar clientes por zona
     */
    public List<ClienteResponse> listarPorZona(int codZona) {
        log.info("Listando clientes de zona: {}", codZona);

        // Verificar que la zona existe
        if (!zonaRepository.existeZona(codZona)) {
            throw new NotFoundException("Zona no encontrada");
        }

        return clienteRepository.listarPorZonaCompleto(codZona);
    }

    /**
     * Buscar clientes por NIT
     */
    public List<ClienteResponse> buscarPorNit(String nit) {
        log.info("Buscando clientes por NIT: {}", nit);
        return clienteRepository.buscarPorNitCompleto(nit);
    }

    /**
     * Buscar clientes por nombre
     */
    public List<ClienteResponse> buscarPorNombre(String nombre) {
        log.info("Buscando clientes por nombre: {}", nombre);
        return clienteRepository.buscarPorNombreCompleto(nombre);
    }

    /**
     * Crear nuevo cliente
     */
    public int crearCliente(ClienteRequest request, int audUsuario) {
        log.info("Creando nuevo cliente: {}", request.getNombreCliente());

        // Verificar que la zona existe
        if (!zonaRepository.existeZona(request.getCodZona())) {
            throw new NotFoundException("Zona no encontrada");
        }

        Cliente cliente = Cliente.builder()
                .codZona(request.getCodZona())
                .nit(request.getNit())
                .razonSocial(request.getRazonSocial())
                .nombreCliente(request.getNombreCliente())
                .direccion(request.getDireccion())
                .referencia(request.getReferencia())
                .obs(request.getObs())
                .audUsuario(audUsuario)
                .build();

        int codCliente = clienteRepository.crearCliente(cliente);

        log.info("Cliente creado exitosamente con ID: {}", codCliente);
        return codCliente;
    }

    /**
     * Actualizar cliente
     */
    public void actualizarCliente(int codCliente, ClienteRequest request, int audUsuario) {
        log.info("Actualizando cliente: {}", codCliente);

        // Verificar que el cliente existe
        clienteRepository.buscarPorIdCompleto(codCliente)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        // Verificar que la zona existe
        if (!zonaRepository.existeZona(request.getCodZona())) {
            throw new NotFoundException("Zona no encontrada");
        }

        Cliente cliente = Cliente.builder()
                .codCliente(codCliente)
                .codZona(request.getCodZona())
                .nit(request.getNit())
                .razonSocial(request.getRazonSocial())
                .nombreCliente(request.getNombreCliente())
                .direccion(request.getDireccion())
                .referencia(request.getReferencia())
                .obs(request.getObs())
                .audUsuario(audUsuario)
                .build();

        clienteRepository.actualizarCliente(cliente);

        log.info("Cliente actualizado exitosamente");
    }

    /**
     * Eliminar cliente
     */
    public void eliminarCliente(int codCliente, int audUsuario) {
        log.info("Eliminando cliente: {}", codCliente);

        // Verificar que el cliente existe
        clienteRepository.buscarPorIdCompleto(codCliente)
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));

        clienteRepository.eliminarCliente(codCliente, audUsuario);

        log.info("Cliente eliminado exitosamente");
    }
}