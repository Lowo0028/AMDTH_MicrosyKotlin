package com.example.catalogo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de Producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del producto")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre del producto", example = "Alimento Premium Perro")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(length = 500)
    @Schema(description = "Descripción del producto", example = "Alimento de alta calidad para perros adultos.")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1000, message = "El precio mínimo permitido es 1000 pesos")
    @Column(nullable = false)
    @Schema(description = "Precio del producto", example = "19990")
    private Double precio;

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    @Schema(description = "Imagen del producto almacenada como BLOB")
    private byte[] imagen;

    @NotBlank(message = "La categoría no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Categoría del producto", example = "Perros")
    private String categoria;
}
