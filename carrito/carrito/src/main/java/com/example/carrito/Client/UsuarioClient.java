package com.example.carrito.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class UsuarioClient {
    
    private final WebClient webClient;
    
    public UsuarioClient(@Value("${auth-service.url}") String authUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(authUrl)
                .build();
    }
    
    public Map<String, Object> obtenerUsuarioPorId(Long id) {
        try {
            return this.webClient.get()
                    .uri("/usuarios/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}