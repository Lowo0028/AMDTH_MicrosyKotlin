package com.example.orden.orden.repository;

import com.example.orden.orden.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuarioIdOrderByCreatedAtDesc(Long usuarioId);
    List<Orden> findAllByOrderByCreatedAtDesc();
}
