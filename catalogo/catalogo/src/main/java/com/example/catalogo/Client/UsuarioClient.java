package com.example.catalogo.Client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final String URL_USUARIOS = "http://localhost:8081/auth";

    public UsuarioClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UsuarioResponse buscarPorCorreo(String correo) {
        try {
            return restTemplate.getForObject(
                    URL_USUARIOS + "/usuario/correo/" + correo,
                    UsuarioResponse.class
            );
        } catch (Exception e) {
            return null; // Usuario no encontrado o error
        }
    }
}
