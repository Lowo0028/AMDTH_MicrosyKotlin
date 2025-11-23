package com.example.orden.orden.controller;

import com.example.orden.orden.model.ItemOrden;
import com.example.orden.orden.model.Orden;
import com.example.orden.orden.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ordenes")
public class OrdenController {
    
    private final OrdenService ordenService;
    
    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }
    
    @Operation(summary = "Obtener órdenes de un usuario")
    @ApiResponse(responseCode = "200", description = "Órdenes obtenidas")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerOrdenesPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<Orden> ordenes = ordenService.obtenerOrdenesPorUsuario(usuarioId);
            return ResponseEntity.ok(ordenes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener items de una orden")
    @ApiResponse(responseCode = "200", description = "Items obtenidos")
    @GetMapping("/{ordenId}/items")
    public ResponseEntity<?> obtenerItemsDeOrden(@PathVariable Long ordenId) {
        try {
            List<ItemOrden> items = ordenService.obtenerItemsDeOrden(ordenId);
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Crear una nueva orden (checkout)")
    @ApiResponse(responseCode = "201", description = "Orden creada")
    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody Map<String, Object> request) {
        try {
            Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
            Double total = Double.valueOf(request.get("total").toString());
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
            
            List<ItemOrden> items = itemsData.stream().map(itemData -> {
                ItemOrden item = new ItemOrden();
                item.setProductoId(Long.valueOf(itemData.get("productoId").toString()));
                item.setProductoNombre((String) itemData.get("productoNombre"));
                
                Object precioObj = itemData.get("productoPrecio");
                Double precio = precioObj instanceof Integer 
                    ? ((Integer) precioObj).doubleValue() 
                    : (Double) precioObj;
                item.setProductoPrecio(precio);
                
                item.setCantidad(Integer.valueOf(itemData.get("cantidad").toString()));
                item.setImageUrl((String) itemData.get("imageUrl"));
                
                return item;
            }).toList();
            
            Orden orden = ordenService.crearOrden(usuarioId, total, items);
            return ResponseEntity.status(201).body(orden);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener una orden por ID")
    @ApiResponse(responseCode = "200", description = "Orden encontrada")
    @GetMapping("/{ordenId}")
    public ResponseEntity<?> obtenerOrdenPorId(@PathVariable Long ordenId) {
        return ordenService.obtenerOrdenPorId(ordenId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Obtener detalles completos de una orden")
    @ApiResponse(responseCode = "200", description = "Detalles obtenidos")
    @GetMapping("/{ordenId}/detalles")
    public ResponseEntity<?> obtenerDetallesOrden(@PathVariable Long ordenId) {
        try {
            Map<String, Object> detalles = ordenService.obtenerDetallesOrden(ordenId);
            return ResponseEntity.ok(detalles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener todas las órdenes (admin)")
    @ApiResponse(responseCode = "200", description = "Todas las órdenes")
    @GetMapping
    public ResponseEntity<List<Orden>> obtenerTodasLasOrdenes() {
        List<Orden> ordenes = ordenService.obtenerTodasLasOrdenes();
        return ResponseEntity.ok(ordenes);
    }
    
    @Operation(summary = "Cancelar una orden")
    @ApiResponse(responseCode = "200", description = "Orden cancelada")
    @PutMapping("/{ordenId}/cancelar")
    public ResponseEntity<?> cancelarOrden(@PathVariable Long ordenId) {
        try {
            Orden orden = ordenService.cancelarOrden(ordenId);
            return ResponseEntity.ok(orden);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}