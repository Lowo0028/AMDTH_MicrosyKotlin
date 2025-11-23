package com.example.orden.orden.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items_orden")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item individual de una orden")
public class ItemOrden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del item")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "ID de la orden a la que pertenece")
    private Long ordenId;
    
    @Column(nullable = false)
    @Schema(description = "ID del producto")
    private Long productoId;
    
    @Column(nullable = false)
    @Schema(description = "Nombre del producto")
    private String productoNombre;
    
    @Column(nullable = false)
    @Schema(description = "Precio unitario del producto al momento de la compra")
    private Double productoPrecio;
    
    @Column(nullable = false)
    @Schema(description = "Cantidad comprada")
    private Integer cantidad;
    
    @Schema(description = "URL de la imagen del producto")
    private String imageUrl;
}
