package com.techmate.techmate.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.CategoriesDTO;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Service.CategoriesService;
import com.techmate.techmate.Validation.ImageValidationStrategy; // Importa la interfaz de validación

import jakarta.validation.Valid;

/**
 * La clase {@code CategoriesController} maneja las solicitudes HTTP
 * relacionadas
 * con las categorías en el sistema.
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173") // Permitir solicitudes desde tu frontend
@RequestMapping("/categories")
@Validated
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private ImageStorageStrategy imageStorageStrategy;

    @Autowired
    private ImageValidationStrategy imageValidationStrategy; // Inyección de la estrategia de validación

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    @GetMapping("/all")
    public ResponseEntity<List<CategoriesDTO>> getAllCategories() {
        try {
            List<CategoriesDTO> categories = categoriesService.getAllCategories().stream()
                    .map(category -> {
                        String imagePath = serverUrl + "/categories/images/" + category.getImagePath();
                        category.setImagePath(imagePath);
                        return category;
                    })
                    .collect(Collectors.toList());
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener categorías
        }
    }

    @PostMapping("/create")
    public ResponseEntity<CategoriesDTO> createCategory(
            @RequestParam("name") @Valid String name,
            @RequestParam("image") MultipartFile image) {
        try {
            // Validar la imagen usando la estrategia de validación
            String imagePath = image.getOriginalFilename();
            imageValidationStrategy.validate(imagePath); // Validación de la extensión

            imagePath = imageStorageStrategy.saveImage(image);

            CategoriesDTO categoryDTO = new CategoriesDTO();
            categoryDTO.setName(name);
            categoryDTO.setImagePath(imagePath);

            CategoriesDTO savedCategory = categoriesService.createCategory(categoryDTO);
            savedCategory.setImagePath(serverUrl + "/categories/images/" + savedCategory.getImagePath());

            return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error al guardar la imagen o categoría
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriesDTO> getCategoryById(@PathVariable("id") Integer id) {
        try {
            CategoriesDTO category = categoriesService.getCategoryById(id);
            if (category == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            category.setImagePath(serverUrl + "/categories/images/" + category.getImagePath());
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener la categoría
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriesDTO> updateCategory(
            @PathVariable("id") Integer id,
            @RequestParam(value = "name", required = false) @Valid String name,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            CategoriesDTO existingCategory = categoriesService.getCategoryById(id);
            if (existingCategory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (name != null) {
                existingCategory.setName(name);
            }

            if (image != null && !image.isEmpty()) {
                // Eliminar la imagen antigua si existe
                String oldImagePath = existingCategory.getImagePath();
                if (oldImagePath != null && !oldImagePath.isEmpty()) {
                    imageStorageStrategy.deleteImage(oldImagePath);
                }

                // Validar la nueva imagen
                String newImagePath = image.getOriginalFilename();
                imageValidationStrategy.validate(newImagePath); // Validación de la extensión

                // Guardar la nueva imagen
                newImagePath = imageStorageStrategy.saveImage(image);
                existingCategory.setImagePath(newImagePath);
            }

            CategoriesDTO updatedCategory = categoriesService.updateCategory(id, existingCategory);
            updatedCategory.setImagePath(serverUrl + "/categories/images/" + updatedCategory.getImagePath());

            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error en la actualización
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {
        try {
            CategoriesDTO category = categoriesService.getCategoryById(id);
            if (category == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Error si la categoría no se encuentra
            }

            // Eliminar la imagen si existe
            String imagePath = category.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia de eliminación
                } catch (RuntimeException e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Manejar errores en la eliminación de la imagen
                }
            }

            categoriesService.deleteCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general al eliminar la categoría
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            byte[] imageBytes = imageStorageStrategy.getImage(filename); // Utiliza el método getImage de la estrategia
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(Paths.get(storageLocation).resolve(filename)))
                    .body(imageBytes);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Imagen no encontrada
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }
}
