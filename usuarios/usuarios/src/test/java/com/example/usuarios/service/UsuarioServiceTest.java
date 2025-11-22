package com.example.usuarios.service;

import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private UsuarioRepository usuarioRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        authService = new AuthService(usuarioRepository, passwordEncoder);
    }

    // ============================================
    //          REGISTRO - EMAIL REPETIDO
    // ============================================
    @Test
    void testRegistrar_EmailRepetido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@correo.com");
        usuario.setTelefono("12345678");

        when(usuarioRepository.existsByEmail("test@correo.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El correo ya está registrado");

        verify(usuarioRepository).existsByEmail("test@correo.com");
        verify(usuarioRepository, never()).save(any());
    }

    // ============================================
    //        REGISTRO - TELÉFONO REPETIDO
    // ============================================
    @Test
    void testRegistrar_TelefonoRepetido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@correo.com");
        usuario.setTelefono("987654321");

        when(usuarioRepository.existsByEmail("nuevo@correo.com"))
                .thenReturn(false);

        when(usuarioRepository.existsByTelefono("987654321"))
                .thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El número de teléfono ya está registrado");
    }

    // ============================================
    //     REGISTRO - TELÉFONO INVÁLIDO
    // ============================================
    @Test
    void testRegistrar_TelefonoInvalido() {
        Usuario usuario = new Usuario();
        usuario.setEmail("nuevo@correo.com");
        usuario.setTelefono("123"); // muy corto

        when(usuarioRepository.existsByEmail("nuevo@correo.com"))
                .thenReturn(false);

        when(usuarioRepository.existsByTelefono("123"))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.registrar(usuario))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El número de teléfono debe tener entre 8 y 12 dígitos.");
    }

    // ============================================
    //          REGISTRO EXITOSO
    // ============================================
    @Test
    void testRegistrar_Exitoso() {

        Usuario usuario = new Usuario();
        usuario.setEmail("correcto@correo.com");
        usuario.setTelefono("987654321");
        usuario.setContrasena("1234");

        when(usuarioRepository.existsByEmail("correcto@correo.com"))
                .thenReturn(false);

        when(usuarioRepository.existsByTelefono("987654321"))
                .thenReturn(false);

        when(passwordEncoder.encode("1234"))
                .thenReturn("ENC_1234");

        when(usuarioRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Usuario guardado = authService.registrar(usuario);

        assertThat(guardado.getContrasena()).isEqualTo("ENC_1234");
        verify(usuarioRepository).save(usuario);
    }

    // ============================================
    //         LOGIN EXITOSO
    // ============================================
    @Test
    void testLogin_Exitoso() {

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@correo.cl");
        usuario.setContrasena("pass_encriptada");

        when(usuarioRepository.findByEmail("admin@correo.cl"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("1234", "pass_encriptada"))
                .thenReturn(true);

        boolean resultado = authService.login("admin@correo.cl", "1234");

        assertThat(resultado).isTrue();
    }

    // ============================================
    //        LOGIN - CONTRASEÑA INCORRECTA
    // ============================================
    @Test
    void testLogin_Incorrecto() {

        Usuario usuario = new Usuario();
        usuario.setEmail("admin@correo.cl");
        usuario.setContrasena("pass_encriptada");

        when(usuarioRepository.findByEmail("admin@correo.cl"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("1234", "pass_encriptada"))
                .thenReturn(false);

        boolean resultado = authService.login("admin@correo.cl", "1234");

        assertThat(resultado).isFalse();
    }

    // ============================================
    //          LOGIN - USUARIO NO EXISTE
    // ============================================
    @Test
    void testLogin_UsuarioNoExiste() {

        when(usuarioRepository.findByEmail("no@existe.cl"))
                .thenReturn(Optional.empty());

        boolean resultado = authService.login("no@existe.cl", "1234");

        assertThat(resultado).isFalse();
    }
}
