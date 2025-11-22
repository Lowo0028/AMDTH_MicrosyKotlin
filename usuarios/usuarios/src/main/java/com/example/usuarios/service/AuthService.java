package com.example.usuarios.service;

import com.example.usuarios.model.Usuario;
import com.example.usuarios.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // ============================
    //      REGISTRO USUARIO
    // ============================
    public Usuario registrar(Usuario usuario) {

        // Validación: email repetido
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // Validación: teléfono repetido
        if (usuarioRepository.existsByTelefono(usuario.getTelefono())) {
            throw new RuntimeException("El número de teléfono ya está registrado");
        }

        // Validación: teléfono no vacío
        if (usuario.getTelefono() == null || usuario.getTelefono().isBlank()) {
            throw new RuntimeException("El número de teléfono no puede estar vacío");
        }

        // Validación: limpiar caracteres para contar dígitos
        String telefonoLimpio = usuario.getTelefono().replaceAll("[^0-9]", "");

        // Validación: longitud válida
        if (telefonoLimpio.length() < 8 || telefonoLimpio.length() > 12) {
            throw new RuntimeException("El número de teléfono debe tener entre 8 y 12 dígitos.");
        }

        // Guardar usuario
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }
    
    // =====================================
    //                LOGIN
    // =====================================
    public boolean login(String email, String contrasena) {

        var usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return false;
        }

        return passwordEncoder.matches(contrasena, usuario.getContrasena());
    }

    // =====================================
    //             LISTAR TODOS
    // =====================================
    public java.util.List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // =====================================
    //            BUSCAR POR ID
    // =====================================
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    
    // =====================================
    //        CREAR ADMIN INICIAL
    // =====================================
    @PostConstruct
    public void crearAdminInicial() {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setEmail("admin@amilimetros.cl");
            admin.setTelefono("+56911111111");
            admin.setContrasena(passwordEncoder.encode("Admin123!"));
            admin.setIsAdmin(true);
            usuarioRepository.save(admin);
        }
    }
}
