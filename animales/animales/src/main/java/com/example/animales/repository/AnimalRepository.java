package com.example.animales.repository;

import com.example.animales.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<Animal> findByEspecie(String especie);
    List<Animal> findByIsAdoptado(Boolean isAdoptado);
    List<Animal> findByNombreContainingIgnoreCase(String nombre);
}