package com.example.formulario.Client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalResponse {
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private String edad;
    private String descripcion;
    private Boolean isAdoptado;
}