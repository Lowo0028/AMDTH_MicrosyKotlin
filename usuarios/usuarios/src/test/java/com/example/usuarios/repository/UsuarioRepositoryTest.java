package com.example.usuarios.repository;

import com.example.usuarios.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void testGuardarYBuscarUsuarioPorEmail() {

        Usuario usuario = new Usuario();
        usuario.setNombre("Marco");
        usuario.setEmail("marco@example.com");
        usuario.setContrasena("1234");
        usuario.setIsAdmin(false);

        usuarioRepository.save(usuario);

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByEmail("marco@example.com");

        assertThat(usuarioEncontrado).isPresent();
        assertThat(usuarioEncontrado.get().getNombre()).isEqualTo("Marco");
    }
}
