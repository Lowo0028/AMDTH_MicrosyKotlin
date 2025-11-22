package com.example.catalogo.service;

import com.example.catalogo.model.Producto;
import com.example.catalogo.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    private ProductoRepository productoRepository;
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        productoRepository = Mockito.mock(ProductoRepository.class);
        productoService = new ProductoService(productoRepository);
    }

    @Test
    void obtenerTodos_delegaAlRepo() {
        when(productoRepository.findAll()).thenReturn(List.of(
                new Producto(1L, "A", "desc", 1000.0, null, "cat"),
                new Producto(2L, "B", "desc2", 2000.0, null, "cat")
        ));

        var lista = productoService.obtenerTodos();

        assertThat(lista).hasSize(2);
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_existente_retornaProducto() {
        Producto p = new Producto(5L, "X", "d", 1500.0, null, "c");
        when(productoRepository.findById(5L)).thenReturn(Optional.of(p));

        Producto res = productoService.obtenerPorId(5L);

        assertThat(res).isNotNull();
        assertThat(res.getNombre()).isEqualTo("X");
    }

    @Test
    void obtenerPorId_noExistente_retornaNull() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        Producto res = productoService.obtenerPorId(99L);
        assertThat(res).isNull();
    }

    @Test
    void crear_guardaYRetornaProducto() {
        Producto nuevo = new Producto(null, "Nuevo", "desc", 1990.0, null, "cat");
        when(productoRepository.save(nuevo)).thenReturn(new Producto(10L, "Nuevo", "desc", 1990.0, null, "cat"));

        Producto res = productoService.crear(nuevo);

        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo(10L);
        verify(productoRepository).save(nuevo);
    }

    @Test
    void actualizar_existente_actualizaYRetorna() {
        Producto existente = new Producto(3L, "Old", "olddesc", 1200.0, null, "A");
        Producto update = new Producto(null, "New", "newdesc", 1300.0, null, "B");

        when(productoRepository.findById(3L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Producto res = productoService.actualizar(3L, update);

        assertThat(res).isNotNull();
        assertThat(res.getNombre()).isEqualTo("New");
        assertThat(res.getCategoria()).isEqualTo("B");
        verify(productoRepository).save(existente);
    }

    @Test
    void actualizar_noExistente_retornaNull() {
        when(productoRepository.findById(777L)).thenReturn(Optional.empty());
        Producto res = productoService.actualizar(777L, new Producto());
        assertThat(res).isNull();
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizarImagen_existente_guardaImagen() {
        Producto p = new Producto(8L, "P", "d", 1500.0, null, "c");
        when(productoRepository.findById(8L)).thenReturn(Optional.of(p));
        byte[] data = new byte[]{1,2,3};

        boolean ok = productoService.actualizarImagen(8L, data);

        assertThat(ok).isTrue();
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        assertThat(captor.getValue().getImagen()).isEqualTo(data);
    }

    @Test
    void actualizarImagen_noExistente_retornaFalse() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        boolean ok = productoService.actualizarImagen(99L, new byte[]{1});
        assertThat(ok).isFalse();
    }

    @Test
    void eliminar_existente_eliminaYRetornaTrue() {
        when(productoRepository.existsById(5L)).thenReturn(true);
        boolean ok = productoService.eliminar(5L);
        assertThat(ok).isTrue();
        verify(productoRepository).deleteById(5L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(productoRepository.existsById(9L)).thenReturn(false);
        boolean ok = productoService.eliminar(9L);
        assertThat(ok).isFalse();
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    void buscarPorNombre_delegaAlRepo() {
        when(productoRepository.findByNombreContainingIgnoreCase("algo"))
                .thenReturn(List.of(new Producto(1L,"algo","d",1000.0,null,"c")));

        var res = productoService.buscarPorNombre("algo");

        assertThat(res).hasSize(1);
        verify(productoRepository).findByNombreContainingIgnoreCase("algo");
    }
}
