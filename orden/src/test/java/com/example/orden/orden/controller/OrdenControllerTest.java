package com.example.orden.orden.controller;

import com.example.orden.orden.model.ItemOrden;
import com.example.orden.orden.model.Orden;
import com.example.orden.orden.service.OrdenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdenController.class)
class OrdenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdenService ordenService;

    @Test
    void obtenerOrdenesPorUsuario_exito() throws Exception {
        when(ordenService.obtenerOrdenesPorUsuario(1L))
                .thenReturn(List.of(new Orden()));

        mockMvc.perform(get("/ordenes/usuario/1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerOrdenesPorUsuario_error() throws Exception {
        when(ordenService.obtenerOrdenesPorUsuario(1L))
                .thenThrow(new IllegalArgumentException("Usuario no encontrado"));

        mockMvc.perform(get("/ordenes/usuario/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }

    @Test
    void obtenerItems_exito() throws Exception {
        when(ordenService.obtenerItemsDeOrden(1L))
                .thenReturn(List.of(new ItemOrden()));

        mockMvc.perform(get("/ordenes/1/items"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerItems_error() throws Exception {
        when(ordenService.obtenerItemsDeOrden(1L))
                .thenThrow(new IllegalArgumentException("Orden no encontrada"));

        mockMvc.perform(get("/ordenes/1/items"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Orden no encontrada"));
    }

    @Test
    void crearOrden_exito() throws Exception {
        Orden orden = new Orden();
        orden.setId(10L);

        when(ordenService.crearOrden(anyLong(), anyDouble(), anyList()))
                .thenReturn(orden);

        String body = """
        {
          "usuarioId": 1,
          "total": 20000,
          "items": [
            {
              "productoId": 99,
              "productoNombre": "Mouse",
              "productoPrecio": 20000,
              "cantidad": 1,
              "imageUrl": "a.png"
            }
          ]
        }
        """;

        mockMvc.perform(post("/ordenes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void crearOrden_error() throws Exception {
        when(ordenService.crearOrden(anyLong(), anyDouble(), anyList()))
                .thenThrow(new IllegalArgumentException("Error en la orden"));

        mockMvc.perform(post("/ordenes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                { "usuarioId": 1, "total": 20000, "items": [] }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error en la orden"));
    }

    @Test
    void obtenerOrdenPorId_exito() throws Exception {
        Orden orden = new Orden();
        orden.setId(1L);

        when(ordenService.obtenerOrdenPorId(1L)).thenReturn(Optional.of(orden));

        mockMvc.perform(get("/ordenes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerOrdenPorId_noExiste() throws Exception {
        when(ordenService.obtenerOrdenPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/ordenes/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelarOrden_exito() throws Exception {
        Orden orden = new Orden();
        orden.setStatus("Cancelada");

        when(ordenService.cancelarOrden(1L)).thenReturn(orden);

        mockMvc.perform(put("/ordenes/1/cancelar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Cancelada"));
    }

    @Test
    void cancelarOrden_error() throws Exception {
        when(ordenService.cancelarOrden(1L))
                .thenThrow(new IllegalArgumentException("La orden ya está cancelada"));

        mockMvc.perform(put("/ordenes/1/cancelar"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La orden ya está cancelada"));
    }
}
