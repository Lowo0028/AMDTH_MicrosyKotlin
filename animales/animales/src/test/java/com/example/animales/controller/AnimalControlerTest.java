package com.example.animales.controller;

import com.example.animales.Client.UsuarioClient;
import com.example.animales.Client.UsuarioResponse;
import com.example.animales.model.Animal;
import com.example.animales.service.AnimalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnimalController.class)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnimalService animalService;

    @MockBean
    private UsuarioClient usuarioClient;

    private UsuarioResponse admin;
    private Animal animal;

    @BeforeEach
    void setUp() {
        admin = new UsuarioResponse();
        admin.setId(1L);
        admin.setNombre("Admin");
        admin.setEmail("admin@amilimetros.cl");
        admin.setTelefono("999999999");
        admin.setIsAdmin(true);

        animal = Animal.builder()
                .id(1L)
                .nombre("Rocky")
                .especie("Perro")
                .raza("Pastor")
                .edad("3 años")
                .descripcion("Muy juguetón")
                .isAdoptado(false)
                .build();
    }

    private void mockAdmin() {
        Mockito.when(usuarioClient.buscarPorCorreo("admin@amilimetros.cl"))
                .thenReturn(admin);
    }

    // ====================================================
    // GET /animales
    // ====================================================
    @Test
    void obtenerTodos_ok() throws Exception {
        Mockito.when(animalService.obtenerTodos()).thenReturn(List.of(animal));

        mockMvc.perform(get("/animales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Rocky"));
    }

    // ====================================================
    // GET /animales/{id}
    // ====================================================
    @Test
    void obtenerPorId_ok() throws Exception {
        Mockito.when(animalService.obtenerPorId(1L)).thenReturn(animal);

        mockMvc.perform(get("/animales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rocky"));
    }

    @Test
    void obtenerPorId_noEncontrado() throws Exception {
        Mockito.when(animalService.obtenerPorId(1L)).thenReturn(null);

        mockMvc.perform(get("/animales/1"))
                .andExpect(status().isNotFound());
    }

    // ====================================================
    // POST /animales (crear)
    // ====================================================
    @Test
    void crearAnimal_adminOk() throws Exception {
        mockAdmin();

        Mockito.when(animalService.crear(any(Animal.class))).thenReturn(animal);

        String json = """
                {
                  "emailAdmin": "admin@amilimetros.cl",
                  "nombre": "Rocky",
                  "especie": "Perro",
                  "raza": "Pastor",
                  "edad": "3 años",
                  "descripcion": "Muy juguetón"
                }
                """;

        mockMvc.perform(post("/animales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Rocky"));
    }

    @Test
    void crearAnimal_adminInvalido() throws Exception {

        Mockito.when(usuarioClient.buscarPorCorreo("admin@amilimetros.cl"))
                .thenReturn(null);

        String json = """
                {
                  "emailAdmin": "admin@amilimetros.cl",
                  "nombre": "Rocky",
                  "especie": "Perro",
                  "raza": "Pastor",
                  "edad": "3 años",
                  "descripcion": "Muy juguetón"
                }
                """;

        mockMvc.perform(post("/animales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    // ====================================================
    // PUT /animales/{id}
    // ====================================================
    @Test
    void actualizar_ok() throws Exception {
        mockAdmin();

        Mockito.when(animalService.actualizar(eq(1L), any(Animal.class))).thenReturn(animal);

        String json = """
                {
                  "emailAdmin": "admin@amilimetros.cl",
                  "nombre": "Rocky",
                  "especie": "Perro",
                  "raza": "Pastor",
                  "edad": "3 años",
                  "descripcion": "Muy juguetón",
                  "isAdoptado": false
                }
                """;

        mockMvc.perform(put("/animales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Rocky"));
    }

    @Test
    void actualizar_noEncontrado() throws Exception {
        mockAdmin();

        Mockito.when(animalService.actualizar(eq(1L), any(Animal.class))).thenReturn(null);

        String json = """
                {
                  "emailAdmin": "admin@amilimetros.cl",
                  "nombre": "Rocky",
                  "especie": "Perro",
                  "raza": "Pastor",
                  "edad": "3 años",
                  "descripcion": "Muy juguetón"
                }
                """;

        mockMvc.perform(put("/animales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // ====================================================
    // DELETE /animales/{id}
    // ====================================================
    @Test
    void eliminar_ok() throws Exception {
        mockAdmin();

        Mockito.when(animalService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/animales/1")
                        .param("emailAdmin", "admin@amilimetros.cl"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_noEncontrado() throws Exception {
        mockAdmin();

        Mockito.when(animalService.eliminar(1L)).thenReturn(false);

        mockMvc.perform(delete("/animales/1")
                        .param("emailAdmin", "admin@amilimetros.cl"))
                .andExpect(status().isNotFound());
    }
}
