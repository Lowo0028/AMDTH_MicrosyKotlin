package com.example.animales.Client;

import lombok.Data;

@Data
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private Boolean isAdmin;
}
