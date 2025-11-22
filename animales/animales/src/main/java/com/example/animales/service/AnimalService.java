package com.example.animales.service;

import com.example.animales.model.Animal;
import com.example.animales.repository.AnimalRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class AnimalService {

    private final AnimalRepository animalRepository;

    public AnimalService(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    public List<Animal> obtenerTodos() {
        return animalRepository.findAll();
    }

    public Animal obtenerPorId(Long id) {
        return animalRepository.findById(id).orElse(null);
    }

    public List<Animal> obtenerPorEspecie(String especie) {
        return animalRepository.findByEspecie(especie);
    }

    public List<Animal> obtenerDisponibles() {
        return animalRepository.findByIsAdoptado(false);
    }

    public List<Animal> obtenerAdoptados() {
        return animalRepository.findByIsAdoptado(true);
    }

    public List<Animal> buscarPorNombre(String nombre) {
        return animalRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Animal crear(Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal actualizar(Long id, Animal animal) {
        Animal existente = animalRepository.findById(id).orElse(null);

        if (existente != null) {
            existente.setNombre(animal.getNombre());
            existente.setEspecie(animal.getEspecie());
            existente.setRaza(animal.getRaza());
            existente.setEdad(animal.getEdad());
            existente.setDescripcion(animal.getDescripcion());
            existente.setIsAdoptado(animal.getIsAdoptado());
            return animalRepository.save(existente);
        }
        return null;
    }

    public boolean actualizarImagen(Long id, byte[] nuevaImagen) {
        Animal animal = animalRepository.findById(id).orElse(null);
        if (animal == null) return false;

        animal.setImagen(nuevaImagen);
        animalRepository.save(animal);
        return true;
    }

    public boolean marcarComoAdoptado(Long id) {
        Animal animal = animalRepository.findById(id).orElse(null);
        if (animal == null) return false;

        animal.setIsAdoptado(true);
        animalRepository.save(animal);
        return true;
    }

    public boolean eliminar(Long id) {
        if (animalRepository.existsById(id)) {
            animalRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private byte[] cargarImagen(String nombre) throws IOException {
        InputStream input = getClass().getClassLoader().getResourceAsStream("static/" + nombre);
        if (input == null) {
            throw new IOException("No se encontró la imagen: " + nombre);
        }
        return input.readAllBytes();
    }

    @PostConstruct
    public void precargarAnimales() {
        if (animalRepository.count() == 0) {
            try {
                Animal a1 = Animal.builder()
                        .nombre("Max")
                        .especie("Perro")
                        .raza("Labrador")
                        .edad("3 años")
                        .descripcion("Perro cariñoso y juguetón, ideal para familias con niños")
                        .imagen(cargarImagen("max.jpg"))
                        .isAdoptado(false)
                        .build();

                Animal a2 = Animal.builder()
                        .nombre("Luna")
                        .especie("Gato")
                        .raza("Siamés")
                        .edad("2 años")
                        .descripcion("Gata tranquila y afectuosa, perfecta para apartamentos")
                        .imagen(cargarImagen("luna.jpg"))
                        .isAdoptado(false)
                        .build();

                Animal a3 = Animal.builder()
                        .nombre("Rocky")
                        .especie("Perro")
                        .raza("Pastor Alemán")
                        .edad("5 años")
                        .descripcion("Perro guardián, entrenado y muy leal")
                        .imagen(cargarImagen("rocky.jpg"))
                        .isAdoptado(false)
                        .build();

                Animal a4 = Animal.builder()
                        .nombre("Mimi")
                        .especie("Gato")
                        .raza("Persa")
                        .edad("1 año")
                        .descripcion("Gatita juguetona y curiosa, le encanta explorar")
                        .imagen(cargarImagen("mimi.jpg"))
                        .isAdoptado(false)
                        .build();

                Animal a5 = Animal.builder()
                        .nombre("Toby")
                        .especie("Perro")
                        .raza("Beagle")
                        .edad("4 años")
                        .descripcion("Perro energético y sociable, excelente con otros animales")
                        .imagen(cargarImagen("toby.jpg"))
                        .isAdoptado(false)
                        .build();

                animalRepository.saveAll(List.of(a1, a2, a3, a4, a5));

                System.out.println("✅ 5 animales precargados exitosamente");

            } catch (IOException e) {
                System.out.println("❌ Error cargando imágenes: " + e.getMessage());
            }
        }
    }
}