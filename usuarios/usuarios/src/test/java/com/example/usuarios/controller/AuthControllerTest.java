package com.example.usuarios.controller;

import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void eliminarUsuario_SinEmailAdmin_Retorna400() throws Exception {

        mockMvc.perform(delete("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Debe enviar el email del admin"));
    }

    @Test
    void eliminarUsuario_AdminNoExiste_Retorna403() throws Exception {

        when(usuarioRepository.findByEmail("admin@duocuc.cl"))
                .thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailAdmin\": \"admin@duocuc.cl\"}"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("No tiene permisos para eliminar usuarios"));
    }

    @Test
    void eliminarUsuario_AdminValidoYUsuarioExiste_RetornaOK() throws Exception {

        Usuario admin = new Usuario();
        admin.setIsAdmin(true);

        when(usuarioRepository.findByEmail("admin@duocuc.cl"))
                .thenReturn(Optional.of(admin));

        when(usuarioRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"emailAdmin\":\"admin@duocuc.cl\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Usuario eliminado correctamente"));
    }
}
