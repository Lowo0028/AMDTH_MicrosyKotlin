package com.example.orden.orden.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ordenes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Orden de compra")
public class Orden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la orden")
    private Long id;
    
    @Column(nullable = false)
    @Schema(description = "ID del usuario que realizó la compra")
    private Long usuarioId;
    
    @Column(nullable = false)
    @Schema(description = "Total de la orden")
    private Double total;
    
    @Column(nullable = false)
    @Schema(description = "Fecha de creación de la orden")
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    @Schema(description = "Estado de la orden: Completada, Cancelada, Pendiente")
    private String status = "Completada";
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
