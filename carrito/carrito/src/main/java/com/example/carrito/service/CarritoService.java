package com.example.carrito.service;

import com.example.carrito.Client.ProductoClient;
import com.example.carrito.Client.UsuarioClient;
import com.example.carrito.model.ItemCarrito;
import com.example.carrito.repository.ItemCarritoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CarritoService {
    
    private final ItemCarritoRepository itemCarritoRepository;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;
    
    public CarritoService(ItemCarritoRepository itemCarritoRepository, 
                         ProductoClient productoClient,
                         UsuarioClient usuarioClient) {
        this.itemCarritoRepository = itemCarritoRepository;
        this.productoClient = productoClient;
        this.usuarioClient = usuarioClient;
    }
    
    // Obtener items del carrito de un usuario
    public List<ItemCarrito> obtenerCarritoPorUsuario(Long usuarioId) {
        // Validar que el usuario existe
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        return itemCarritoRepository.findByUsuarioId(usuarioId);
    }
    
    // Agregar producto al carrito
    @Transactional
    public ItemCarrito agregarAlCarrito(Long usuarioId, Long productoId, Integer cantidad) {
        // Validar que el usuario existe
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        // Validar que el producto existe en el microservicio de Catálogo
        Map<String, Object> producto = productoClient.obtenerProductoPorId(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        
        // Verificar si el producto ya está en el carrito
        Optional<ItemCarrito> itemExistente = itemCarritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);
        
        if (itemExistente.isPresent()) {
            // Actualizar cantidad
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + cantidad);
            return itemCarritoRepository.save(item);
        } else {
            // Crear nuevo item
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setUsuarioId(usuarioId);
            nuevoItem.setProductoId(productoId);
            nuevoItem.setProductoNombre((String) producto.get("nombre"));
            
            // Manejo seguro del precio (puede venir como Integer o Double)
            Object precioObj = producto.get("precio");
            Double precio = precioObj instanceof Integer 
                ? ((Integer) precioObj).doubleValue() 
                : (Double) precioObj;
            nuevoItem.setProductoPrecio(precio);
            
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setImageUrl((String) producto.get("imageUrl"));
            
            return itemCarritoRepository.save(nuevoItem);
        }
    }
    
    // Actualizar cantidad de un item
    public Optional<ItemCarrito> actualizarCantidad(Long itemId, Integer nuevaCantidad) {
        Optional<ItemCarrito> item = itemCarritoRepository.findById(itemId);
        
        if (item.isPresent()) {
            ItemCarrito itemActual = item.get();
            
            if (nuevaCantidad <= 0) {
                itemCarritoRepository.delete(itemActual);
                return Optional.empty();
            }
            
            itemActual.setCantidad(nuevaCantidad);
            return Optional.of(itemCarritoRepository.save(itemActual));
        }
        
        return Optional.empty();
    }
    
    // Eliminar item del carrito
    public boolean eliminarItem(Long itemId) {
        if (itemCarritoRepository.existsById(itemId)) {
            itemCarritoRepository.deleteById(itemId);
            return true;
        }
        return false;
    }
    
    // Vaciar carrito completo de un usuario
    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        // Validar que el usuario existe
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        itemCarritoRepository.deleteByUsuarioId(usuarioId);
    }
    
    // Calcular total del carrito
    public Double calcularTotal(Long usuarioId) {
        // Validar que el usuario existe
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        Double total = itemCarritoRepository.calcularTotal(usuarioId);
        return total != null ? total : 0.0;
    }
    
    // Método auxiliar para obtener información del usuario y producto juntos
    public Map<String, Object> obtenerDetallesCarrito(Long usuarioId) {
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        List<ItemCarrito> items = itemCarritoRepository.findByUsuarioId(usuarioId);
        Double total = calcularTotal(usuarioId);
        
        return Map.of(
            "usuario", usuario,
            "items", items,
            "total", total,
            "cantidadItems", items.size()
        );
    }
}