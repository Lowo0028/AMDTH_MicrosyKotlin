package com.example.usuarios.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Modelo de Usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario")
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Nombre completo del usuario")
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    @Schema(description = "Correo electrónico único")
    private String email;

    @NotBlank(message = "El teléfono no puede estar vacío")
    @Column(nullable = false)
    @Schema(description = "Teléfono de contacto")
    private String telefono;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(nullable = false)
    @Schema(description = "Contraseña encriptada")
    private String contrasena;

    @Column(nullable = false)
    @Schema(description = "Indica si es administrador")
    private Boolean isAdmin = false;
}
