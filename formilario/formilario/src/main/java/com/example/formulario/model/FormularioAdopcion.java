package com.example.formulario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "formularios_adopcion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Formulario de Adopción de Animales")
public class FormularioAdopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del formulario")
    private Long id;

    // SE GUARDA EN LA BD (viene de la URL, no del body)
    @Column(nullable = false)
    @Schema(description = "ID del usuario que solicita la adopción")
    private Long usuarioId;

    // SE GUARDA EN LA BD (viene de la URL, no del body)
    @Column(nullable = false)
    @Schema(description = "ID del animal a adoptar")
    private Long animalId;

    @NotBlank(message = "La dirección no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Dirección del solicitante", example = "Av. Principal 123, Depto 45")
    private String direccion;

    @NotBlank(message = "El tipo de vivienda no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Tipo de vivienda", example = "Departamento")
    private String tipoVivienda;

    @NotNull(message = "Debe indicar si tiene mallas en ventanas")
    @Column(nullable = false)
    @Schema(description = "¿Tiene mallas en las ventanas?", example = "true")
    private Boolean tieneMallasVentanas;

    @NotNull(message = "Debe indicar si vive en departamento")
    @Column(nullable = false)
    @Schema(description = "¿Vive en departamento?", example = "true")
    private Boolean viveEnDepartamento;

    @NotNull(message = "Debe indicar si tiene otros animales")
    @Column(nullable = false)
    @Schema(description = "¿Tiene otros animales en casa?", example = "false")
    private Boolean tieneOtrosAnimales;

    @Column(length = 500)
    @Schema(description = "Motivo de la adopción", example = "Quiero darle un hogar cariñoso")
    private String motivoAdopcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Estado del formulario", example = "PENDIENTE")
    private EstadoFormulario estado = EstadoFormulario.PENDIENTE;

    @Column(length = 500)
    @Schema(description = "Comentarios del administrador sobre la solicitud")
    private String comentariosAdmin;

    @Column(nullable = false)
    @Schema(description = "Fecha de creación del formulario")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de revisión del formulario")
    private LocalDateTime fechaRevision;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}