package com.example.formulario.repository;

import com.example.formulario.model.EstadoFormulario;
import com.example.formulario.model.FormularioAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormularioAdopcionRepository extends JpaRepository<FormularioAdopcion, Long> {
    List<FormularioAdopcion> findByUsuarioId(Long usuarioId);
    List<FormularioAdopcion> findByAnimalId(Long animalId);
    List<FormularioAdopcion> findByEstado(EstadoFormulario estado);
    Optional<FormularioAdopcion> findByUsuarioIdAndAnimalIdAndEstado(Long usuarioId, Long animalId, EstadoFormulario estado);
}