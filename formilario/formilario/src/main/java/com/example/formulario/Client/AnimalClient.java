package com.example.formulario.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AnimalClient {

    private final WebClient webClient;

    public AnimalClient(@Value("${animales-service.url}") String animalesUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(animalesUrl)
                .build();
    }

    public AnimalResponse obtenerAnimalPorId(Long id) {
        try {
            return this.webClient.get()
                    .uri("/animales/{id}", id)
                    .retrieve()
                    .bodyToMono(AnimalResponse.class)
                    .block();
        } catch (Exception e) {
            return null;
        }
    }
}