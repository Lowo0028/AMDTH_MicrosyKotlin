package com.example.formulario.controller;

import com.example.formulario.Client.UsuarioClient;
import com.example.formulario.Client.UsuarioResponse;
import com.example.formulario.model.EstadoFormulario;
import com.example.formulario.model.FormularioAdopcion;
import com.example.formulario.service.FormularioAdopcionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/formularios")
public class FormularioAdopcionController {

    private final FormularioAdopcionService formularioService;
    private final UsuarioClient usuarioClient;

    public FormularioAdopcionController(FormularioAdopcionService formularioService,
                                       UsuarioClient usuarioClient) {
        this.formularioService = formularioService;
        this.usuarioClient = usuarioClient;
    }

    // ============================
    // VALIDAR ADMIN
    // ============================
    private boolean esAdmin(String correo) {
        UsuarioResponse user = usuarioClient.buscarPorCorreo(correo);
        return user != null && Boolean.TRUE.equals(user.getIsAdmin());
    }

    // ============================
    // OBTENER TODOS (solo admin)
    // ============================
    @Operation(summary = "Obtener todos los formularios")
    @ApiResponse(responseCode = "200", description = "Formularios obtenidos")
    @GetMapping
    public ResponseEntity<?> obtenerTodos(@RequestParam("emailAdmin") String emailAdmin) {
        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para ver todos los formularios");
        }
        
        return ResponseEntity.ok(formularioService.obtenerTodos());
    }

    // ============================
    // OBTENER POR ID
    // ============================
    @Operation(summary = "Obtener formulario por ID")
    @ApiResponse(responseCode = "200", description = "Formulario encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<FormularioAdopcion> obtenerPorId(@PathVariable Long id) {
        FormularioAdopcion formulario = formularioService.obtenerPorId(id);
        return formulario != null ? ResponseEntity.ok(formulario) : ResponseEntity.notFound().build();
    }

    // ============================
    // OBTENER POR USUARIO
    // ============================
    @Operation(summary = "Obtener formularios de un usuario")
    @ApiResponse(responseCode = "200", description = "Formularios del usuario")
    @GetMapping("/usuario/{usuarioId}")
    public List<FormularioAdopcion> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return formularioService.obtenerPorUsuario(usuarioId);
    }

    // ============================
    // OBTENER POR ANIMAL
    // ============================
    @Operation(summary = "Obtener formularios de un animal")
    @ApiResponse(responseCode = "200", description = "Formularios del animal")
    @GetMapping("/animal/{animalId}")
    public List<FormularioAdopcion> obtenerPorAnimal(@PathVariable Long animalId) {
        return formularioService.obtenerPorAnimal(animalId);
    }

    // ============================
    // OBTENER POR ESTADO
    // ============================
    @Operation(summary = "Obtener formularios por estado")
    @ApiResponse(responseCode = "200", description = "Formularios filtrados")
    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> obtenerPorEstado(
            @PathVariable String estado,
            @RequestParam("emailAdmin") String emailAdmin
    ) {
        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para filtrar formularios");
        }

        try {
            EstadoFormulario estadoEnum = EstadoFormulario.valueOf(estado.toUpperCase());
            return ResponseEntity.ok(formularioService.obtenerPorEstado(estadoEnum));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado inválido. Use: PENDIENTE, APROBADO o RECHAZADO");
        }
    }

    // ============================
    // CREAR FORMULARIO - DESDE LA APP
    // usuarioId viene del login (app lo tiene guardado)
    // animalId viene del botón "Adoptar" que clickeó
    // ============================
    @Operation(summary = "Crear formulario de adopción (usuario autenticado hace clic en adoptar)")
    @ApiResponse(responseCode = "201", description = "Formulario creado")
    @PostMapping("/adoptar/{usuarioId}/{animalId}")
    public ResponseEntity<?> crearFormulario(
            @PathVariable Long usuarioId,
            @PathVariable Long animalId,
            @RequestBody Map<String, Object> body
    ) {
        try {
            // Extraer datos del body (solo lo que el usuario llena en el formulario)
            String direccion = (String) body.get("direccion");
            String tipoVivienda = (String) body.get("tipoVivienda");
            Boolean tieneMallasVentanas = Boolean.valueOf(body.get("tieneMallasVentanas").toString());
            Boolean viveEnDepartamento = Boolean.valueOf(body.get("viveEnDepartamento").toString());
            Boolean tieneOtrosAnimales = Boolean.valueOf(body.get("tieneOtrosAnimales").toString());
            String motivoAdopcion = (String) body.get("motivoAdopcion");

            // Validaciones básicas
            if (direccion == null || direccion.isBlank()) {
                return ResponseEntity.badRequest().body("La dirección es obligatoria");
            }
            if (tipoVivienda == null || tipoVivienda.isBlank()) {
                return ResponseEntity.badRequest().body("El tipo de vivienda es obligatorio");
            }

            // Construir el formulario con los IDs que vienen de la URL
            FormularioAdopcion formulario = FormularioAdopcion.builder()
                    .usuarioId(usuarioId)
                    .animalId(animalId)
                    .direccion(direccion)
                    .tipoVivienda(tipoVivienda)
                    .tieneMallasVentanas(tieneMallasVentanas)
                    .viveEnDepartamento(viveEnDepartamento)
                    .tieneOtrosAnimales(tieneOtrosAnimales)
                    .motivoAdopcion(motivoAdopcion)
                    .build();

            FormularioAdopcion nuevo = formularioService.crear(formulario);
            return ResponseEntity.status(201).body(nuevo);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al procesar el formulario: " + e.getMessage());
        }
    }

    // ============================
    // APROBAR FORMULARIO (solo admin)
    // ============================
    @Operation(summary = "Aprobar formulario de adopción")
    @ApiResponse(responseCode = "200", description = "Formulario aprobado")
    @PutMapping("/{id}/aprobar")
    public ResponseEntity<?> aprobar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String emailAdmin = body.get("emailAdmin");
        String comentarios = body.get("comentarios");

        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para aprobar formularios");
        }

        FormularioAdopcion aprobado = formularioService.aprobar(id, comentarios);
        return aprobado != null ? ResponseEntity.ok(aprobado) : ResponseEntity.notFound().build();
    }

    // ============================
    // RECHAZAR FORMULARIO (solo admin)
    // ============================
    @Operation(summary = "Rechazar formulario de adopción")
    @ApiResponse(responseCode = "200", description = "Formulario rechazado")
    @PutMapping("/{id}/rechazar")
    public ResponseEntity<?> rechazar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String emailAdmin = body.get("emailAdmin");
        String comentarios = body.get("comentarios");

        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para rechazar formularios");
        }

        FormularioAdopcion rechazado = formularioService.rechazar(id, comentarios);
        return rechazado != null ? ResponseEntity.ok(rechazado) : ResponseEntity.notFound().build();
    }

    // ============================
    // ELIMINAR FORMULARIO (solo admin)
    // ============================
    @Operation(summary = "Eliminar formulario")
    @ApiResponse(responseCode = "204", description = "Formulario eliminado")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id,
            @RequestParam("emailAdmin") String emailAdmin
    ) {
        if (!esAdmin(emailAdmin)) {
            return ResponseEntity.status(403).body("No tienes permisos para eliminar formularios");
        }

        return formularioService.eliminar(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}