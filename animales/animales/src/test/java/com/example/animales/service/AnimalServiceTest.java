package com.example.animales.service;

import com.example.animales.model.Animal;
import com.example.animales.repository.AnimalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnimalServiceTest {

    private AnimalRepository animalRepository;
    private AnimalService animalService;

    @BeforeEach
    void setup() {
        animalRepository = mock(AnimalRepository.class);
        animalService = new AnimalService(animalRepository);
    }

    // ===============================================================
    // 1) OBTENER TODOS
    // ===============================================================
    @Test
    void obtenerTodosTest() {
        when(animalRepository.findAll()).thenReturn(List.of(new Animal(), new Animal()));

        List<Animal> lista = animalService.obtenerTodos();

        assertEquals(2, lista.size());
        verify(animalRepository, times(1)).findAll();
    }

    // ===============================================================
    // 2) OBTENER POR ID
    // ===============================================================
    @Test
    void obtenerPorId_Encontrado() {

        Animal a = new Animal();
        a.setId(1L);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(a));

        Animal resultado = animalService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void obtenerPorId_NoEncontrado() {

        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        Animal resultado = animalService.obtenerPorId(1L);

        assertNull(resultado);
    }

    // ===============================================================
    // 3) CREAR ANIMAL
    // ===============================================================
    @Test
    void crearAnimalTest() {

        Animal animal = new Animal();
        animal.setNombre("Luna");

        when(animalRepository.save(animal)).thenReturn(animal);

        Animal resultado = animalService.crear(animal);

        assertEquals("Luna", resultado.getNombre());
        verify(animalRepository).save(animal);
    }

    // ===============================================================
    // 4) ACTUALIZAR ANIMAL
    // ===============================================================
    @Test
    void actualizarAnimal_Exitoso() {

        Animal existente = new Animal();
        existente.setId(1L);
        existente.setNombre("Viejo");

        Animal cambios = new Animal();
        cambios.setNombre("Nuevo");
        cambios.setEspecie("Perro");
        cambios.setRaza("Beagle");
        cambios.setEdad("3 años");
        cambios.setDescripcion("Muy bueno");
        cambios.setIsAdoptado(false);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(animalRepository.save(any())).thenAnswer(invocado -> invocado.getArgument(0));

        Animal resultado = animalService.actualizar(1L, cambios);

        assertNotNull(resultado);
        assertEquals("Nuevo", resultado.getNombre());
        assertEquals("Perro", resultado.getEspecie());
        assertEquals("Beagle", resultado.getRaza());
        verify(animalRepository).save(existente);
    }

    @Test
    void actualizarAnimal_NoExiste() {

        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        Animal resultado = animalService.actualizar(1L, new Animal());

        assertNull(resultado);
    }

    // ===============================================================
    // 5) ACTUALIZAR IMAGEN
    // ===============================================================
    @Test
    void actualizarImagen_Exitoso() {

        Animal existente = new Animal();
        existente.setId(1L);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));

        byte[] imagen = new byte[]{1, 2, 3};
        boolean ok = animalService.actualizarImagen(1L, imagen);

        assertTrue(ok);
        assertArrayEquals(imagen, existente.getImagen());
        verify(animalRepository).save(existente);
    }

    @Test
    void actualizarImagen_NoExiste() {

        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        boolean ok = animalService.actualizarImagen(1L, new byte[]{1, 2});

        assertFalse(ok);
        verify(animalRepository, never()).save(any());
    }

    // ===============================================================
    // 6) MARCAR COMO ADOPTADO
    // ===============================================================
    @Test
    void marcarComoAdoptado_Exitoso() {

        Animal existente = new Animal();
        existente.setId(1L);
        existente.setIsAdoptado(false);

        when(animalRepository.findById(1L)).thenReturn(Optional.of(existente));

        boolean ok = animalService.marcarComoAdoptado(1L);

        assertTrue(ok);
        assertTrue(existente.getIsAdoptado());
        verify(animalRepository).save(existente);
    }

    @Test
    void marcarComoAdoptado_NoExiste() {

        when(animalRepository.findById(1L)).thenReturn(Optional.empty());

        boolean ok = animalService.marcarComoAdoptado(1L);

        assertFalse(ok);
    }

    // ===============================================================
    // 7) ELIMINAR
    // ===============================================================
    @Test
    void eliminar_Exitoso() {

        when(animalRepository.existsById(1L)).thenReturn(true);

        boolean ok = animalService.eliminar(1L);

        assertTrue(ok);
        verify(animalRepository).deleteById(1L);
    }

    @Test
    void eliminar_NoExiste() {

        when(animalRepository.existsById(1L)).thenReturn(false);

        boolean ok = animalService.eliminar(1L);

        assertFalse(ok);
        verify(animalRepository, never()).deleteById(anyLong());
    }
}
