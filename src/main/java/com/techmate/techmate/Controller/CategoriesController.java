package com.techmate.techmate.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.CategoriesDTO;
import com.techmate.techmate.Exception.ErrorResponse;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Service.CategoriesService;
import com.techmate.techmate.Validation.ImageValidationStrategy; // Importa la interfaz de validación

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * La clase {@code CategoriesController} maneja las solicitudes HTTP
 * relacionadas
 * con las categorías en el sistema.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
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

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(
            @Valid @ModelAttribute CategoriesDTO categoriesDTO,
            @RequestParam("image") MultipartFile image,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // Filtrar solo los errores relevantes
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .filter(error -> "name".equals(error.getField()) || "imagePath".equals(error.getField())) // Filtra
                                                                                                              // los
                                                                                                              // campos
                                                                                                              // específicos
                    .map(error -> error.getDefaultMessage()) // Obtener el mensaje de error
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new ErrorResponse(errorMessages), HttpStatus.BAD_REQUEST); // Retorna errores de
                                                                                                   // validación
        }

        try {
            // Delegar la validación y almacenamiento de la imagen al servicio
            CategoriesDTO savedCategory = categoriesService.createCategory(categoriesDTO, image);

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

    // Endpoint para actualizar una categoría

    // Endpoint para actualizar una categoría
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute CategoriesDTO categoriesDTO,
            @RequestParam(value = "image", required = false) MultipartFile image,
            BindingResult bindingResult) { // Agregamos BindingResult

        if (bindingResult.hasErrors()) {
            // Filtrar solo los errores relevantes
            List<String> errorMessages = bindingResult.getFieldErrors()
                    .stream()
                    .filter(error -> "name".equals(error.getField()) || "imagePath".equals(error.getField())) // Filtra
                                                                                                              // los
                                                                                                              // campos
                                                                                                              // específicos
                    .map(error -> error.getDefaultMessage()) // Obtener el mensaje de error
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new ErrorResponse(errorMessages), HttpStatus.BAD_REQUEST); // Retorna errores de
                                                                                                   // validación
        }

        try {
            // Delegar la actualización de la categoría al servicio
            CategoriesDTO updatedCategory = categoriesService.updateCategory(id, categoriesDTO, image);

            if (updatedCategory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // Actualizar el path de la imagen con la URL completa del servidor
            updatedCategory.setImagePath(serverUrl + "/categories/images/" + updatedCategory.getImagePath());
            return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error en la actualización
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

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

            if (categories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No hay subcategorías
            }
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener categorías
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {
        try {
            // Delegar la eliminación al servicio directamente
            categoriesService.deleteCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Categoría eliminada correctamente
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Error si la categoría no se encuentra
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error en la eliminación de la imagen o
                                                                           // categoría
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            byte[] imageBytes = imageStorageStrategy.getImage(filename); // Utiliza el método getImage de la estrategia
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE,
                            Files.probeContentType(Paths.get(storageLocation).resolve(filename)))
                    .body(imageBytes);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Imagen no encontrada
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }
}
