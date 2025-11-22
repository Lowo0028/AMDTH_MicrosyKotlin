package com.example.formulario.service;

import com.example.formulario.Client.AnimalClient;
import com.example.formulario.Client.AnimalResponse;
import com.example.formulario.Client.UsuarioClient;
import com.example.formulario.Client.UsuarioResponse;
import com.example.formulario.model.EstadoFormulario;
import com.example.formulario.model.FormularioAdopcion;
import com.example.formulario.repository.FormularioAdopcionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FormularioAdopcionService {

    private final FormularioAdopcionRepository formularioRepository;
    private final UsuarioClient usuarioClient;
    private final AnimalClient animalClient;

    public FormularioAdopcionService(FormularioAdopcionRepository formularioRepository,
                                    UsuarioClient usuarioClient,
                                    AnimalClient animalClient) {
        this.formularioRepository = formularioRepository;
        this.usuarioClient = usuarioClient;
        this.animalClient = animalClient;
    }

    // Obtener todos los formularios
    public List<FormularioAdopcion> obtenerTodos() {
        return formularioRepository.findAll();
    }

    // Obtener formulario por ID
    public FormularioAdopcion obtenerPorId(Long id) {
        return formularioRepository.findById(id).orElse(null);
    }

    // Obtener formularios de un usuario
    public List<FormularioAdopcion> obtenerPorUsuario(Long usuarioId) {
        return formularioRepository.findByUsuarioId(usuarioId);
    }

    // Obtener formularios de un animal
    public List<FormularioAdopcion> obtenerPorAnimal(Long animalId) {
        return formularioRepository.findByAnimalId(animalId);
    }

    // Obtener formularios por estado
    public List<FormularioAdopcion> obtenerPorEstado(EstadoFormulario estado) {
        return formularioRepository.findByEstado(estado);
    }

    // Crear nuevo formulario
    public FormularioAdopcion crear(FormularioAdopcion formulario) {
        // Validar que el usuario existe
        UsuarioResponse usuario = usuarioClient.obtenerUsuarioPorId(formulario.getUsuarioId());
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Validar que el animal existe
        AnimalResponse animal = animalClient.obtenerAnimalPorId(formulario.getAnimalId());
        if (animal == null) {
            throw new IllegalArgumentException("Animal no encontrado");
        }

        // Validar que el animal no esté adoptado
        if (animal.getIsAdoptado()) {
            throw new IllegalArgumentException("El animal ya está adoptado");
        }

        // Validar que el usuario no tenga ya una solicitud pendiente para este animal
        Optional<FormularioAdopcion> existente = formularioRepository
                .findByUsuarioIdAndAnimalIdAndEstado(
                        formulario.getUsuarioId(),
                        formulario.getAnimalId(),
                        EstadoFormulario.PENDIENTE
                );

        if (existente.isPresent()) {
            throw new IllegalArgumentException("Ya tienes una solicitud pendiente para este animal");
        }

        formulario.setEstado(EstadoFormulario.PENDIENTE);
        return formularioRepository.save(formulario);
    }

    // Aprobar formulario (solo admin)
    public FormularioAdopcion aprobar(Long id, String comentariosAdmin) {
        FormularioAdopcion formulario = formularioRepository.findById(id).orElse(null);
        
        if (formulario == null) {
            return null;
        }

        formulario.setEstado(EstadoFormulario.APROBADO);
        formulario.setComentariosAdmin(comentariosAdmin);
        formulario.setFechaRevision(LocalDateTime.now());

        return formularioRepository.save(formulario);
    }

    // Rechazar formulario (solo admin)
    public FormularioAdopcion rechazar(Long id, String comentariosAdmin) {
        FormularioAdopcion formulario = formularioRepository.findById(id).orElse(null);
        
        if (formulario == null) {
            return null;
        }

        formulario.setEstado(EstadoFormulario.RECHAZADO);
        formulario.setComentariosAdmin(comentariosAdmin);
        formulario.setFechaRevision(LocalDateTime.now());

        return formularioRepository.save(formulario);
    }

    // Eliminar formulario
    public boolean eliminar(Long id) {
        if (formularioRepository.existsById(id)) {
            formularioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}