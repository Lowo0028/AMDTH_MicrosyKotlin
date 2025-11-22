package com.example.carrito.controller;

import com.example.carrito.model.ItemCarrito;
import com.example.carrito.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/carrito")
public class CarritoController {
    
    private final CarritoService carritoService;
    
    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }
    
    @Operation(summary = "Obtener carrito de un usuario")
    @ApiResponse(responseCode = "200", description = "Carrito obtenido")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerCarrito(@PathVariable Long usuarioId) {
        try {
            List<ItemCarrito> items = carritoService.obtenerCarritoPorUsuario(usuarioId);
            return ResponseEntity.ok(items);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Obtener detalles completos del carrito (usuario + items + total)")
    @ApiResponse(responseCode = "200", description = "Detalles obtenidos")
    @GetMapping("/usuario/{usuarioId}/detalles")
    public ResponseEntity<?> obtenerDetallesCarrito(@PathVariable Long usuarioId) {
        try {
            Map<String, Object> detalles = carritoService.obtenerDetallesCarrito(usuarioId);
            return ResponseEntity.ok(detalles);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Agregar producto al carrito")
    @ApiResponse(responseCode = "201", description = "Producto agregado")
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestBody Map<String, Object> request) {
        try {
            Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
            Long productoId = Long.valueOf(request.get("productoId").toString());
            Integer cantidad = Integer.valueOf(request.get("cantidad").toString());
            
            ItemCarrito item = carritoService.agregarAlCarrito(usuarioId, productoId, cantidad);
            return ResponseEntity.status(201).body(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Actualizar cantidad de un item")
    @ApiResponse(responseCode = "200", description = "Cantidad actualizada")
    @PutMapping("/item/{itemId}")
    public ResponseEntity<ItemCarrito> actualizarCantidad(
            @PathVariable Long itemId, 
            @RequestBody Map<String, Integer> request) {
        Integer nuevaCantidad = request.get("cantidad");
        
        return carritoService.actualizarCantidad(itemId, nuevaCantidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Eliminar item del carrito")
    @ApiResponse(responseCode = "204", description = "Item eliminado")
    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long itemId) {
        return carritoService.eliminarItem(itemId) 
                ? ResponseEntity.noContent().build() 
                : ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "Vaciar carrito completo")
    @ApiResponse(responseCode = "204", description = "Carrito vaciado")
    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> vaciarCarrito(@PathVariable Long usuarioId) {
        try {
            carritoService.vaciarCarrito(usuarioId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @Operation(summary = "Calcular total del carrito")
    @ApiResponse(responseCode = "200", description = "Total calculado")
    @GetMapping("/usuario/{usuarioId}/total")
    public ResponseEntity<?> calcularTotal(@PathVariable Long usuarioId) {
        try {
            Double total = carritoService.calcularTotal(usuarioId);
            return ResponseEntity.ok(Map.of("total", total));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}