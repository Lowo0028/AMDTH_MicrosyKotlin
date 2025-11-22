package com.example.animales.controller;

import com.example.animales.Client.UsuarioClient;
import com.example.animales.Client.UsuarioResponse;
import com.example.animales.model.Animal;
import com.example.animales.service.AnimalService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/animales")
public class AnimalController {

    private final AnimalService animalService;
    private final UsuarioClient usuarioClient;

    public AnimalController(AnimalService animalService, UsuarioClient usuarioClient) {
        this.animalService = animalService;
        this.usuarioClient = usuarioClient;
    }

    // ============================
    // OBTENER TODOS
    // ============================
    @GetMapping
    public List<Animal> obtenerTodos() {
        return animalService.obtenerTodos();
    }

    // ============================
    // OBTENER DISPONIBLES (no adoptados)
    // ============================
    @GetMapping("/disponibles")
    public List<Animal> obtenerDisponibles() {
        return animalService.obtenerDisponibles();
    }

    // ============================
    // OBTENER ADOPTADOS
    // ============================
    @GetMapping("/adoptados")
    public List<Animal> obtenerAdoptados() {
        return animalService.obtenerAdoptados();
    }

    // ============================
    // OBTENER POR ID
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<Animal> obtenerPorId(@PathVariable Long id) {
        Animal animal = animalService.obtenerPorId(id);
        return animal != null ? ResponseEntity.ok(animal) : ResponseEntity.notFound().build();
    }

    // ============================
    // OBTENER POR ESPECIE
    // ============================
    @GetMapping("/especie/{especie}")
    public List<Animal> obtenerPorEspecie(@PathVariable String especie) {
        return animalService.obtenerPorEspecie(especie);
    }

    // ============================
    // BUSCAR POR NOMBRE
    // ============================
    @GetMapping("/buscar")
    public List<Animal> buscarPorNombre(@RequestParam String nombre) {
        return animalService.buscarPorNombre(nombre);
    }

    // ============================
    // VALIDAR ADMIN
    // ============================
   private boolean esAdmin(String correo) {
    UsuarioResponse user = usuarioClient.buscarPorCorreo(correo);
    return user != null && Boolean.TRUE.equals(user.getIsAdmin());
}

    // ============================
    // CREAR ANIMAL (solo admin)
    // ============================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {

        String emailAdmin = (String) body.get("emailAdmin");

        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para agregar animales");
        }

        if (body.get("nombre") == null || ((String) body.get("nombre")).isBlank())
            return ResponseEntity.badRequest().body("El nombre no puede estar vacío");

        if (body.get("especie") == null || ((String) body.get("especie")).isBlank())
            return ResponseEntity.badRequest().body("La especie no puede estar vacía");

        if (body.get("raza") == null || ((String) body.get("raza")).isBlank())
            return ResponseEntity.badRequest().body("La raza no puede estar vacía");

        if (body.get("descripcion") == null || ((String) body.get("descripcion")).isBlank())
            return ResponseEntity.badRequest().body("La descripción no puede estar vacía");

        if (body.get("edad") == null || ((String) body.get("edad")).isBlank())
            return ResponseEntity.badRequest().body("La edad es obligatoria");

        Animal animal = Animal.builder()
                .nombre((String) body.get("nombre"))
                .especie((String) body.get("especie"))
                .raza((String) body.get("raza"))
                .edad((String) body.get("edad"))
                .descripcion((String) body.get("descripcion"))
                .isAdoptado(false)
                .build();

        return ResponseEntity.status(201).body(animalService.crear(animal));
    }

    // ============================
    // ACTUALIZAR ANIMAL (solo admin)
    // ============================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        String emailAdmin = (String) body.get("emailAdmin");

        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para actualizar animales");
        }

        if (body.get("nombre") == null || ((String) body.get("nombre")).isBlank())
            return ResponseEntity.badRequest().body("El nombre no puede estar vacío");

        if (body.get("especie") == null || ((String) body.get("especie")).isBlank())
            return ResponseEntity.badRequest().body("La especie no puede estar vacía");

        if (body.get("raza") == null || ((String) body.get("raza")).isBlank())
            return ResponseEntity.badRequest().body("La raza no puede estar vacía");

        if (body.get("descripcion") == null || ((String) body.get("descripcion")).isBlank())
            return ResponseEntity.badRequest().body("La descripción no puede estar vacía");

        if (body.get("edad") == null || ((String) body.get("edad")).isBlank())
            return ResponseEntity.badRequest().body("La edad es obligatoria");

        Boolean isAdoptado = body.get("isAdoptado") != null 
            ? Boolean.valueOf(body.get("isAdoptado").toString()) 
            : false;

        Animal animal = Animal.builder()
                .nombre((String) body.get("nombre"))
                .especie((String) body.get("especie"))
                .raza((String) body.get("raza"))
                .edad((String) body.get("edad"))
                .descripcion((String) body.get("descripcion"))
                .isAdoptado(isAdoptado)
                .build();

        Animal actualizado = animalService.actualizar(id, animal);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    // ============================
    // ACTUALIZAR IMAGEN (solo admin)
    // ============================
    @PostMapping("/{id}/imagen")
    public ResponseEntity<?> actualizarImagen(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("emailAdmin") String emailAdmin
    ) {
        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para actualizar imágenes");
        }

        try {
            boolean ok = animalService.actualizarImagen(id, file.getBytes());
            return ok ? ResponseEntity.ok("Imagen actualizada correctamente")
                    : ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error al leer la imagen");
        }
    }

    // ============================
    // OBTENER IMAGEN
    // ============================
    @GetMapping(
            value = "/{id}/imagen",
            produces = {
                    MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_PNG_VALUE,
                    MediaType.IMAGE_GIF_VALUE
            }
    )
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        Animal animal = animalService.obtenerPorId(id);

        if (animal == null || animal.getImagen() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(animal.getImagen());
    }

    // ============================
    // MARCAR COMO ADOPTADO (solo admin)
    // ============================
    @PutMapping("/{id}/adoptar")
    public ResponseEntity<?> marcarComoAdoptado(
            @PathVariable Long id,
            @RequestParam("emailAdmin") String emailAdmin
    ) {
        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para marcar animales como adoptados");
        }

        boolean ok = animalService.marcarComoAdoptado(id);
        return ok ? ResponseEntity.ok("Animal marcado como adoptado")
                : ResponseEntity.notFound().build();
    }

    // ============================
    // ELIMINAR ANIMAL (solo admin)
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id,
            @RequestParam("emailAdmin") String emailAdmin
    ) {
        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para eliminar animales");
        }

        return animalService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}