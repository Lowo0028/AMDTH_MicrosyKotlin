package com.example.catalogo.service;

import com.example.catalogo.model.Producto;
import com.example.catalogo.repository.ProductoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    
    public List<Producto> buscarPorNombre(String nombre) {
    return productoRepository.findByNombreContainingIgnoreCase(nombre);
}

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    public List<Producto> obtenerPorCategoria(String categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    public Producto crear(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        Producto existente = productoRepository.findById(id).orElse(null);

        if (existente != null) {
            existente.setNombre(producto.getNombre());
            existente.setDescripcion(producto.getDescripcion());
            existente.setPrecio(producto.getPrecio());
            existente.setCategoria(producto.getCategoria());
            return productoRepository.save(existente);
        }
        return null;
    }

    public boolean actualizarImagen(Long id, byte[] nuevaImagen) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) return false;

        producto.setImagen(nuevaImagen);
        productoRepository.save(producto);
        return true;
    }

    public boolean eliminar(Long id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

   
    private byte[] cargarImagen(String nombre) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("static/" + nombre);
        if (input == null) {
            throw new IOException("No se encontró la imagen: " + nombre);
        }
        return input.readAllBytes();
    }

 
    @PostConstruct
    public void precargarProductos() {
        if (productoRepository.count() == 0) {

            try {
                Producto p1 = new Producto(null, "Alimento Premium Perro", "Alimento balanceado 15kg", 35990.0,
                        cargarImagen("comidaperro.webp"), "Alimento");

                Producto p2 = new Producto(null, "Pelota Interactiva", "Pelota de goma con sonido", 8990.0,
                        cargarImagen("pelota.jpg"), "Juguetes");

                Producto p3 = new Producto(null, "Collar Ajustable", "Collar de nylon para perros", 6990.0,
                        cargarImagen("collar.webp"), "Accesorios");

                Producto p4 = new Producto(null, "Arena para Gatos", "Arena aglomerante 10kg", 12990.0,
                        cargarImagen("arena.jpg"), "Alimento");

                Producto p5 = new Producto(null, "Rascador para Gatos", "Rascador de sisal", 25990.0,
                        cargarImagen("rascador.jpg"), "Accesorios");

                productoRepository.saveAll(List.of(p1, p2, p3, p4, p5));

            } catch (IOException e) {
                System.out.println("Error cargando imágenes: " + e.getMessage());
            }
        }
    }
}
