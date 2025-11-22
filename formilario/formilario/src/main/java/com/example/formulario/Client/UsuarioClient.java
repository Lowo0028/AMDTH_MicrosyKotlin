package com.example.formulario.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Value("${auth-service.url}") String authUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(authUrl)
                .build();
    }

    public UsuarioResponse obtenerUsuarioPorId(Long id) {
        try {
            return this.webClient.get()
                    .uri("/auth/usuarios/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }

    public UsuarioResponse buscarPorCorreo(String correo) {
        try {
            return this.webClient.get()
                    .uri("/auth/usuario/correo/{correo}", correo)  
                    .retrieve()
                    .bodyToMono(UsuarioResponse.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}