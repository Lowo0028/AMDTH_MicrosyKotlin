package com.example.usuarios.repository;

import com.example.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    boolean existsByTelefono(String telefono);

    java.util.Optional<Usuario> findByEmail(String email);
}
