package com.example.carrito.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item del carrito de compras")
public class ItemCarrito {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del item")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "ID del usuario dueño del carrito")
    private Long usuarioId;
    
    @Column(nullable = false)
    @Schema(description = "ID del producto")
    private Long productoId;
    
    @Column(nullable = false)
    @Schema(description = "Nombre del producto")
    private String productoNombre;
    
    @Column(nullable = false)
    @Schema(description = "Precio unitario del producto")
    private Double productoPrecio;
    
    @Column(nullable = false)
    @Schema(description = "Cantidad de productos")
    private Integer cantidad;
    
    @Schema(description = "URL de la imagen del producto")
    private String imageUrl;
}