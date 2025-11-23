package com.yahveh.service;

import com.yahveh.dto.request.PersonaRequest;
import com.yahveh.dto.response.PersonaResponse;
import com.yahveh.exception.BusinessException;
import com.yahveh.model.Persona;
import com.yahveh.repository.PersonaRepository;
import com.yahveh.security.SecurityUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Slf4j
public class PersonaService {

    @Inject
    PersonaRepository personaRepository;

    @Inject
    SecurityUtils securityUtils;

    public List<PersonaResponse> listar() {
        log.info("Listando todas las personas");
        return personaRepository.listarTodas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PersonaResponse buscarPorCodigo(long codPersona) {
        log.info("Buscando persona: {}", codPersona);
        Persona persona = personaRepository.buscarPorCodigo(codPersona)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada"));
        return toResponse(persona);
    }

    public PersonaResponse buscarPorCI(String ciNumero, String ciExpedido) {
        log.info("Buscando persona por CI: {} {}", ciNumero, ciExpedido);
        Persona persona = personaRepository.buscarPorCI(ciNumero, ciExpedido)
                .orElseThrow(() -> new NotFoundException("Persona no encontrada"));
        return toResponse(persona);
    }

    public List<PersonaResponse> buscarPorNombre(String nombre) {
        log.info("Buscando personas por nombre: {}", nombre);
        return personaRepository.buscarPorNombre(nombre).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PersonaResponse crear(PersonaRequest request) {
        log.info("Creando persona: {} {}", request.getNombres(), request.getApPaterno());

        validarRequest(request);

        long audUsuario = securityUtils.getCurrentUserId();

        Persona persona = Persona.builder()
                .nombres(request.getNombres())
                .apPaterno(request.getApPaterno())
                .apMaterno(request.getApMaterno())
                .ciNumero(request.getCiNumero())
                .ciExpedido(request.getCiExpedido())
                .ciFechaVencimiento(request.getCiFechaVencimiento())
                .direccion(request.getDireccion())
                .estadoCivil(request.getEstadoCivil())
                .fechaNacimiento(request.getFechaNacimiento())
                .lugarNacimiento(request.getLugarNacimiento())
                .sexo(request.getSexo())
                .audUsuario(audUsuario)
                .build();

        long codPersona = personaRepository.crearPersona(persona);

        return buscarPorCodigo(codPersona);
    }

    public PersonaResponse actualizar(long codPersona, PersonaRequest request) {
        log.info("Actualizando persona: {}", codPersona);

        // Validar que existe
        buscarPorCodigo(codPersona);

        validarRequest(request);

        long audUsuario = securityUtils.getCurrentUserId();

        Persona persona = Persona.builder()
                .codPersona(codPersona)
                .nombres(request.getNombres())
                .apPaterno(request.getApPaterno())
                .apMaterno(request.getApMaterno())
                .ciNumero(request.getCiNumero())
                .ciExpedido(request.getCiExpedido())
                .ciFechaVencimiento(request.getCiFechaVencimiento())
                .direccion(request.getDireccion())
                .estadoCivil(request.getEstadoCivil())
                .fechaNacimiento(request.getFechaNacimiento())
                .lugarNacimiento(request.getLugarNacimiento())
                .sexo(request.getSexo())
                .audUsuario(audUsuario)
                .build();

        personaRepository.actualizarPersona(persona);

        return buscarPorCodigo(codPersona);
    }

    public void eliminar(long codPersona) {
        log.info("Eliminando persona: {}", codPersona);

        // Validar que existe
        buscarPorCodigo(codPersona);

        long audUsuario = securityUtils.getCurrentUserId();

        personaRepository.eliminarPersona(codPersona, audUsuario);
    }

    private void validarRequest(PersonaRequest request) {
        if (request.getNombres() == null || request.getNombres().trim().isEmpty()) {
            throw new BusinessException("El nombre es obligatorio");
        }

        if (request.getApPaterno() == null || request.getApPaterno().trim().isEmpty()) {
            throw new BusinessException("El apellido paterno es obligatorio");
        }

        if (request.getCiNumero() == null || request.getCiNumero().trim().isEmpty()) {
            throw new BusinessException("El número de CI es obligatorio");
        }

        if (request.getCiExpedido() == null || request.getCiExpedido().trim().isEmpty()) {
            throw new BusinessException("El lugar de expedición del CI es obligatorio");
        }

        if (!request.getCiExpedido().matches("^(LP|CB|SC|OR|PT|TJ|CH|BE|PD)$")) {
            throw new BusinessException("Lugar de expedición inválido. Use: LP, CB, SC, OR, PT, TJ, CH, BE, PD");
        }

        if (request.getSexo() == null || request.getSexo().trim().isEmpty()) {
            throw new BusinessException("El sexo es obligatorio");
        }

        if (!request.getSexo().matches("^[MF]$")) {
            throw new BusinessException("El sexo debe ser M (Masculino) o F (Femenino)");
        }

        if (request.getEstadoCivil() != null && !request.getEstadoCivil().matches("^[SCDV]$")) {
            throw new BusinessException("Estado civil inválido. Use: S (Soltero), C (Casado), D (Divorciado), V (Viudo)");
        }

        // Validar fecha de nacimiento
        if (request.getFechaNacimiento() != null) {
            if (request.getFechaNacimiento().isAfter(LocalDate.now())) {
                throw new BusinessException("La fecha de nacimiento no puede ser futura");
            }

            int edad = Period.between(request.getFechaNacimiento(), LocalDate.now()).getYears();
            if (edad < 18) {
                throw new BusinessException("La persona debe ser mayor de 18 años");
            }
        }

        // Validar fecha de vencimiento CI
        if (request.getCiFechaVencimiento() != null) {
            if (request.getCiFechaVencimiento().isBefore(LocalDate.now())) {
                log.warn("CI vencido para: {} {}", request.getNombres(), request.getApPaterno());
            }
        }
    }

    private PersonaResponse toResponse(Persona persona) {
        PersonaResponse response = PersonaResponse.builder()
                .codPersona(persona.getCodPersona())
                .nombres(persona.getNombres())
                .apPaterno(persona.getApPaterno())
                .apMaterno(persona.getApMaterno())
                .ciNumero(persona.getCiNumero())
                .ciExpedido(persona.getCiExpedido())
                .ciFechaVencimiento(persona.getCiFechaVencimiento())
                .direccion(persona.getDireccion())
                .estadoCivil(persona.getEstadoCivil())
                .fechaNacimiento(persona.getFechaNacimiento())
                .lugarNacimiento(persona.getLugarNacimiento())
                .sexo(persona.getSexo())
                .audUsuario(persona.getAudUsuario())
                .build();

        // Nombre completo
        StringBuilder nombreCompleto = new StringBuilder(persona.getNombres());
        nombreCompleto.append(" ").append(persona.getApPaterno());
        if (persona.getApMaterno() != null && !persona.getApMaterno().trim().isEmpty()) {
            nombreCompleto.append(" ").append(persona.getApMaterno());
        }
        response.setNombreCompleto(nombreCompleto.toString().trim());

        // CI completo
        response.setCiCompleto(persona.getCiNumero() + " " + persona.getCiExpedido());

        // Calcular edad
        if (persona.getFechaNacimiento() != null) {
            response.setEdad(Period.between(persona.getFechaNacimiento(), LocalDate.now()).getYears());
        }

        // Descripciones
        response.setSexoDescripcion(getSexoDescripcion(persona.getSexo()));
        response.setEstadoCivilDescripcion(getEstadoCivilDescripcion(persona.getEstadoCivil()));

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

    private String getEstadoCivilDescripcion(String estadoCivil) {
        if (estadoCivil == null) return "No especificado";
        return switch (estadoCivil) {
            case "S" -> "Soltero/a";
            case "C" -> "Casado/a";
            case "D" -> "Divorciado/a";
            case "V" -> "Viudo/a";
            default -> "No especificado";
        };
    }
}