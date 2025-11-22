package com.example.formulario.controller;

import com.example.formulario.Client.UsuarioClient;
import com.example.formulario.Client.UsuarioResponse;
import com.example.formulario.model.EstadoFormulario;
import com.example.formulario.model.FormularioAdopcion;
import com.example.formulario.service.FormularioAdopcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FormularioAdopcionController.class)
public class FormularioAdopcionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FormularioAdopcionService formularioService;

    @MockBean
    private UsuarioClient usuarioClient;

    private UsuarioResponse admin;

    @BeforeEach
    void setup() {
        admin = UsuarioResponse.builder()
                .id(1L)
                .nombre("Admin")
                .email("admin@amilimetros.cl")
                .telefono("0000")
                .isAdmin(true)
                .build();
    }

    private void mockAdmin() {
        when(usuarioClient.buscarPorCorreo("admin@amilimetros.cl"))
                .thenReturn(admin);
    }

    // ============================
    // GET /formularios (ADMIN)
    // ============================
    @Test
    void obtenerTodos_AdminOK() throws Exception {
        mockAdmin();

        when(formularioService.obtenerTodos())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/formularios")
                .param("emailAdmin", "admin@amilimetros.cl"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerTodos_NoEsAdmin() throws Exception {
        when(usuarioClient.buscarPorCorreo(anyString())).thenReturn(null);

        mockMvc.perform(get("/formularios")
                .param("emailAdmin", "x@x.com"))
                .andExpect(status().isForbidden());
    }

    // ============================
    // GET /formularios/{id}
    // ============================
    @Test
    void obtenerPorId_OK() throws Exception {
        FormularioAdopcion form = FormularioAdopcion.builder()
                .id(1L)
                .usuarioId(10L)
                .animalId(20L)
                .build();

        when(formularioService.obtenerPorId(1L)).thenReturn(form);

        mockMvc.perform(get("/formularios/1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorId_NotFound() throws Exception {
        when(formularioService.obtenerPorId(1L)).thenReturn(null);

        mockMvc.perform(get("/formularios/1"))
                .andExpect(status().isNotFound());
    }

    // ============================
    // GET POR ESTADO (ADMIN)
    // ============================
    @Test
    void obtenerPorEstado_AdminOK() throws Exception {
        mockAdmin();

        when(formularioService.obtenerPorEstado(EstadoFormulario.PENDIENTE))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/formularios/estado/PENDIENTE")
                .param("emailAdmin", "admin@amilimetros.cl"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorEstado_EstadoInvalido() throws Exception {
        mockAdmin();

        mockMvc.perform(get("/formularios/estado/NOEXISTE")
                .param("emailAdmin", "admin@amilimetros.cl"))
                .andExpect(status().isBadRequest());
    }

    // ============================
    // POST CREAR FORMULARIO
    // ============================
    @Test
    void crearFormulario_OK() throws Exception {
        FormularioAdopcion created = FormularioAdopcion.builder()
                .id(1L)
                .build();

        when(formularioService.crear(any())).thenReturn(created);

        mockMvc.perform(post("/formularios/adoptar/10/20")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "direccion":"Casa 1",
                          "tipoVivienda": "Casa",
                          "tieneMallasVentanas": true,
                          "viveEnDepartamento": false,
                          "tieneOtrosAnimales": false,
                          "motivoAdopcion": "Quiero adoptar"
                        }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void crearFormulario_DireccionVacia() throws Exception {
        mockMvc.perform(post("/formularios/adoptar/10/20")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "direccion":"",
                          "tipoVivienda": "Casa",
                          "tieneMallasVentanas": true,
                          "viveEnDepartamento": false,
                          "tieneOtrosAnimales": false,
                          "motivoAdopcion": "Quiero adoptar"
                        }
                        """))
                .andExpect(status().isBadRequest());
    }

    // ============================
    // PUT APROBAR (ADMIN)
    // ============================
    @Test
    void aprobar_AdminOK() throws Exception {
        mockAdmin();

        when(formularioService.aprobar(eq(1L), anyString()))
                .thenReturn(FormularioAdopcion.builder().id(1L).build());

        mockMvc.perform(put("/formularios/1/aprobar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"emailAdmin":"admin@amilimetros.cl","comentarios":"OK"}
                        """))
                .andExpect(status().isOk());
    }

    // ============================
    // PUT RECHAZAR (ADMIN)
    // ============================
    @Test
    void rechazar_AdminOK() throws Exception {
        mockAdmin();

        when(formularioService.rechazar(eq(1L), anyString()))
                .thenReturn(FormularioAdopcion.builder().id(1L).build());

        mockMvc.perform(put("/formularios/1/rechazar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"emailAdmin":"admin@amilimetros.cl","comentarios":"Mal"}
                        """))
                .andExpect(status().isOk());
    }

    // ============================
    // DELETE (ADMIN)
    // ============================
    @Test
    void eliminar_AdminOK() throws Exception {
        mockAdmin();

        when(formularioService.eliminar(1L)).thenReturn(true);

        mockMvc.perform(delete("/formularios/1")
                .param("emailAdmin", "admin@amilimetros.cl"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_NotFound() throws Exception {
        mockAdmin();

        when(formularioService.eliminar(1L)).thenReturn(false);

        mockMvc.perform(delete("/formularios/1")
                .param("emailAdmin", "admin@amilimetros.cl"))
                .andExpect(status().isNotFound());
    }
}
