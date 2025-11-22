package com.example.carrito.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class ProductoClient {
    
    private final WebClient webClient;
    
    public ProductoClient(@Value("${catalogo-service.url}") String catalogoUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(catalogoUrl)
                .build();
    }
    
    public Map<String, Object> obtenerProductoPorId(Long id) {
        try {
            return this.webClient.get()
                    .uri("/productos/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}