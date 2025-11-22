package com.example.animales.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "animales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Modelo de Animal para Adopción")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del animal")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre del animal", example = "Max")
    private String nombre;

    @NotBlank(message = "La especie no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Especie del animal", example = "Perro")
    private String especie;

    @NotBlank(message = "La raza no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Raza del animal", example = "Labrador")
    private String raza;

    @NotBlank(message = "La edad es obligatoria")
    @Column(nullable = false)
    @Schema(description = "Edad del animal", example = "3 años")
    private String edad;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(length = 500)
    @Schema(description = "Descripción del animal", example = "Perro cariñoso y juguetón")
    private String descripcion;

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB")
    @Schema(description = "Imagen del animal almacenada como BLOB")
    private byte[] imagen;

    @Column(nullable = false)
    @Schema(description = "Estado de adopción", example = "false")
    private Boolean isAdoptado = false;
}