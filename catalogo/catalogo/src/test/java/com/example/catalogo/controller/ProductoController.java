package com.example.catalogo.controller;

import com.example.catalogo.Client.UsuarioClient;
import com.example.catalogo.Client.UsuarioResponse;
import com.example.catalogo.model.Producto;
import com.example.catalogo.service.ProductoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final UsuarioClient usuarioClient;

    public ProductoController(ProductoService productoService, UsuarioClient usuarioClient) {
        this.productoService = productoService;
        this.usuarioClient = usuarioClient;
    }

    // ============================
    //  OBTENER TODOS
    // ============================
    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoService.obtenerTodos();
    }

    // ============================
    //  OBTENER POR ID
    // ============================
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoService.obtenerPorId(id);
        return (producto != null)
                ? ResponseEntity.ok(producto)
                : ResponseEntity.notFound().build();
    }

    // ============================
    //  OBTENER POR CATEGORIA
    // ============================
    @GetMapping("/categoria/{categoria}")
    public List<Producto> obtenerPorCategoria(@PathVariable String categoria) {
        return productoService.obtenerPorCategoria(categoria);
    }

    // ============================
    //  VALIDAR ADMIN
    // ============================
    private boolean esAdmin(String email) {
        UsuarioResponse user = usuarioClient.buscarPorCorreo(email);
        return user != null && "ADMIN".equalsIgnoreCase(user.getRol());
    }

    // ============================
    //  CREAR PRODUCTO (ADMIN)
    // ============================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {

        String emailAdmin = (String) body.get("emailAdmin");

        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para crear productos");
        }

        // VALIDACIONES
        if (body.get("nombre") == null || ((String) body.get("nombre")).isBlank())
            return ResponseEntity.badRequest().body("El nombre no puede estar vacío");

        if (body.get("descripcion") == null || ((String) body.get("descripcion")).isBlank())
            return ResponseEntity.badRequest().body("La descripción no puede estar vacía");

        if (body.get("categoria") == null || ((String) body.get("categoria")).isBlank())
            return ResponseEntity.badRequest().body("La categoría no puede estar vacía");

        if (body.get("precio") == null)
            return ResponseEntity.badRequest().body("El precio es obligatorio");

        Double precio = Double.valueOf(body.get("precio").toString());
        if (precio < 1000)
            return ResponseEntity.badRequest().body("El precio mínimo es 1000 pesos");

        Producto producto = new Producto();
        producto.setNombre((String) body.get("nombre"));
        producto.setDescripcion((String) body.get("descripcion"));
        producto.setCategoria((String) body.get("categoria"));
        producto.setPrecio(precio);

        Producto creado = productoService.crear(producto);
        return ResponseEntity.status(201).body(creado);
    }

    // ============================
    //  ACTUALIZAR PRODUCTO (ADMIN)
    // ============================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {

        String emailAdmin = (String) body.get("emailAdmin");

        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para actualizar productos");
        }

        // VALIDACIONES
        if (body.get("nombre") == null || ((String) body.get("nombre")).isBlank())
            return ResponseEntity.badRequest().body("El nombre no puede estar vacío");

        if (body.get("descripcion") == null || ((String) body.get("descripcion")).isBlank())
            return ResponseEntity.badRequest().body("La descripción no puede estar vacía");

        if (body.get("categoria") == null || ((String) body.get("categoria")).isBlank())
            return ResponseEntity.badRequest().body("La categoría no puede estar vacía");

        if (body.get("precio") == null)
            return ResponseEntity.badRequest().body("El precio es obligatorio");

        Double precio = Double.valueOf(body.get("precio").toString());
        if (precio < 1000)
            return ResponseEntity.badRequest().body("El precio mínimo es 1000 pesos");

        Producto producto = new Producto();
        producto.setNombre((String) body.get("nombre"));
        producto.setDescripcion((String) body.get("descripcion"));
        producto.setCategoria((String) body.get("categoria"));
        producto.setPrecio(precio);

        Producto actualizado = productoService.actualizar(id, producto);
        return (actualizado != null)
                ? ResponseEntity.ok(actualizado)
                : ResponseEntity.notFound().build();
    }

    // ============================
    //  ACTUALIZAR IMAGEN (ADMIN)
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
            boolean ok = productoService.actualizarImagen(id, file.getBytes());
            return ok
                    ? ResponseEntity.ok("Imagen actualizada correctamente")
                    : ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error al procesar la imagen");
        }
    }

    // ============================
    //  VER IMAGEN
    // ============================
    @GetMapping(value = "/{id}/imagen",
            produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {

        Producto producto = productoService.obtenerPorId(id);

        if (producto == null || producto.getImagen() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(producto.getImagen());
    }

    // ============================
    //  ELIMINAR PRODUCTO (ADMIN)
    // ============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam("emailAdmin") String emailAdmin) {

        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para eliminar productos");
        }

        return productoService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // ============================
    //  BUSCAR POR NOMBRE
    // ============================
    @GetMapping("/buscar")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre);
    }
}
