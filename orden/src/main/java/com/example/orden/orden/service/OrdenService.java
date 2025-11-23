package com.example.orden.orden.service;
import com.example.orden.orden.Client.UsuarioClient;
import com.example.orden.orden.model.ItemOrden;
import com.example.orden.orden.model.Orden;
import com.example.orden.orden.repository.ItemOrdenRepository;
import com.example.orden.orden.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrdenService {
    
    private final OrdenRepository ordenRepository;
    private final ItemOrdenRepository itemOrdenRepository;
    private final UsuarioClient usuarioClient;
    
    public OrdenService(OrdenRepository ordenRepository,
                       ItemOrdenRepository itemOrdenRepository,
                       UsuarioClient usuarioClient) {
        this.ordenRepository = ordenRepository;
        this.itemOrdenRepository = itemOrdenRepository;
        this.usuarioClient = usuarioClient;
    }
    
    public List<Orden> obtenerOrdenesPorUsuario(Long usuarioId) {
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        return ordenRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
    }
    
    public List<ItemOrden> obtenerItemsDeOrden(Long ordenId) {
        if (!ordenRepository.existsById(ordenId)) {
            throw new IllegalArgumentException("Orden no encontrada");
        }
        return itemOrdenRepository.findByOrdenId(ordenId);
    }
    
    @Transactional
    public Orden crearOrden(Long usuarioId, Double total, List<ItemOrden> items) {
        Map<String, Object> usuario = usuarioClient.obtenerUsuarioPorId(usuarioId);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("La orden debe tener al menos un item");
        }
        
        Orden orden = new Orden();
        orden.setUsuarioId(usuarioId);
        orden.setTotal(total);
        orden.setStatus("Completada");
        
        orden = ordenRepository.save(orden);
        
        final Long ordenId = orden.getId();
        items.forEach(item -> item.setOrdenId(ordenId));
        itemOrdenRepository.saveAll(items);
        
        return orden;
    }
    
    public Optional<Orden> obtenerOrdenPorId(Long ordenId) {
        return ordenRepository.findById(ordenId);
    }
    
    public Map<String, Object> obtenerDetallesOrden(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        
        List<ItemOrden> items = itemOrdenRepository.findByOrdenId(ordenId);
        
        return Map.of(
            "orden", orden,
            "items", items,
            "cantidadItems", items.size()
        );
    }
    
    public List<Orden> obtenerTodasLasOrdenes() {
        return ordenRepository.findAllByOrderByCreatedAtDesc();
    }
    
    @Transactional
    public Orden cancelarOrden(Long ordenId) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        
        if ("Cancelada".equals(orden.getStatus())) {
            throw new IllegalArgumentException("La orden ya está cancelada");
        }
        
        orden.setStatus("Cancelada");
        return ordenRepository.save(orden);
    }
}