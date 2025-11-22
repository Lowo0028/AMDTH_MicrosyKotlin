package com.example.formulario.service;

import com.example.formulario.Client.AnimalClient;
import com.example.formulario.Client.AnimalResponse;
import com.example.formulario.Client.UsuarioClient;
import com.example.formulario.Client.UsuarioResponse;
import com.example.formulario.model.EstadoFormulario;
import com.example.formulario.model.FormularioAdopcion;
import com.example.formulario.repository.FormularioAdopcionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
public class FormularioAdopcionServiceTest {

    @Mock
    private FormularioAdopcionRepository repository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private AnimalClient animalClient;

    @InjectMocks
    private FormularioAdopcionService service;

    private UsuarioResponse usuario;
    private AnimalResponse animal;

    @BeforeEach
    void setup() {
        usuario = UsuarioResponse.builder()
                .id(1L)
                .nombre("Usuario")
                .email("test@mail.com")
                .isAdmin(false)
                .build();

        animal = AnimalResponse.builder()
                .id(1L)
                .nombre("Firulais")
                .isAdoptado(false)
                .build();
    }

    @Test
    void crear_OK() {
        FormularioAdopcion f = FormularioAdopcion.builder()
                .usuarioId(1L)
                .animalId(1L)
                .build();

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuario);
        when(animalClient.obtenerAnimalPorId(1L)).thenReturn(animal);
        when(repository.findByUsuarioIdAndAnimalIdAndEstado(1L, 1L, EstadoFormulario.PENDIENTE))
                .thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(f);

        FormularioAdopcion result = service.crear(f);

        assertNotNull(result);
        assertEquals(EstadoFormulario.PENDIENTE, result.getEstado());
    }

    @Test
    void crear_UsuarioNoExiste() {
        FormularioAdopcion f = FormularioAdopcion.builder()
                .usuarioId(1L)
                .animalId(1L)
                .build();

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.crear(f));
    }

    @Test
    void crear_AnimalNoExiste() {
        FormularioAdopcion f = FormularioAdopcion.builder()
                .usuarioId(1L)
                .animalId(1L)
                .build();

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuario);
        when(animalClient.obtenerAnimalPorId(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.crear(f));
    }

    @Test
    void crear_AnimalAdoptado() {
        FormularioAdopcion f = FormularioAdopcion.builder()
                .usuarioId(1L)
                .animalId(1L)
                .build();

        animal.setIsAdoptado(true);

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuario);
        when(animalClient.obtenerAnimalPorId(1L)).thenReturn(animal);

        assertThrows(IllegalArgumentException.class, () -> service.crear(f));
    }

    @Test
    void crear_SolicitudYaPendiente() {
        FormularioAdopcion f = FormularioAdopcion.builder()
                .usuarioId(1L)
                .animalId(1L)
                .build();

        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(usuario);
        when(animalClient.obtenerAnimalPorId(1L)).thenReturn(animal);
        when(repository.findByUsuarioIdAndAnimalIdAndEstado(1L, 1L, EstadoFormulario.PENDIENTE))
                .thenReturn(Optional.of(new FormularioAdopcion()));

        assertThrows(IllegalArgumentException.class, () -> service.crear(f));
    }

    @Test
    void aprobar_OK() {
        FormularioAdopcion f = new FormularioAdopcion();
        when(repository.findById(1L)).thenReturn(Optional.of(f));
        when(repository.save(any())).thenReturn(f);

        FormularioAdopcion res = service.aprobar(1L, "OK");

        assertNotNull(res);
        assertEquals(EstadoFormulario.APROBADO, res.getEstado());
    }

    @Test
    void aprobar_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertNull(service.aprobar(1L, "OK"));
    }

    @Test
    void rechazar_OK() {
        FormularioAdopcion f = new FormularioAdopcion();
        when(repository.findById(1L)).thenReturn(Optional.of(f));
        when(repository.save(any())).thenReturn(f);

        FormularioAdopcion res = service.rechazar(1L, "Mal");

        assertNotNull(res);
        assertEquals(EstadoFormulario.RECHAZADO, res.getEstado());
    }

    @Test
    void eliminar_OK() {
        when(repository.existsById(1L)).thenReturn(true);
        boolean ok = service.eliminar(1L);
        assertTrue(ok);
    }

    @Test
    void eliminar_NotFound() {
        when(repository.existsById(1L)).thenReturn(false);
        boolean ok = service.eliminar(1L);
        assertFalse(ok);
    }
}
