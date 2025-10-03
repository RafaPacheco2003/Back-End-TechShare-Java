package com.techmate.techmate.Controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Service.CategoriesService;
// ...existing code...
import com.techmate.techmate.dto.CategoriesDTO;
import com.techmate.techmate.dto.CategoryRequest;
import com.techmate.techmate.dto.CategoryResponse;
import com.techmate.techmate.dto.ErrorResponse;
import com.techmate.techmate.Service.categories.mapper.CategoriesMapper;

import jakarta.validation.Valid;

/**
 * La clase {@code CategoriesController} maneja las solicitudes HTTP
 * relacionadas
 * con las categorías en el sistema.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
@RequestMapping("admin/categories")
@Validated
public class CategoriesController {

    private final CategoriesService categoriesService;
    private final ImageStorageStrategy imageStorageStrategy;
    private final CategoriesMapper categoriesMapper;
    // ...existing code... (image validation handled elsewhere)
    private final String storageLocation; // Directorio para almacenar imágenes
    private final String serverUrl; // URL base del servidor

    public CategoriesController(CategoriesService categoriesService,
            ImageStorageStrategy imageStorageStrategy,
            CategoriesMapper categoriesMapper,
            @Value("${storage.location}") String storageLocation,
            @Value("${server.url}") String serverUrl) {
        this.categoriesService = categoriesService;
        this.imageStorageStrategy = imageStorageStrategy;
        this.categoriesMapper = categoriesMapper;
        this.storageLocation = storageLocation;
        this.serverUrl = serverUrl;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategory(
        @Valid @ModelAttribute CategoryRequest categoriesRequest,
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
            // Mapear request -> internal DTO usando mapper
            CategoriesDTO dto = categoriesMapper.fromRequest(categoriesRequest);
            CategoriesDTO savedCategory = categoriesService.createCategory(dto, image);

            // Mapear a response público usando mapper
            CategoryResponse response = categoriesMapper.toResponse(savedCategory, serverUrl);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (com.techmate.techmate.exception.BusinessException e) {
            // Devolver el mensaje de negocio como ErrorResponse para que el cliente lo reciba
            return new ResponseEntity<>(new ErrorResponse(List.of(e.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("id") Integer id) {
        try {
            CategoriesDTO category = categoriesService.getCategoryById(id);
            if (category == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            CategoryResponse resp = categoriesMapper.toResponse(category, serverUrl);

            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener la categoría
        }
    }

    // Endpoint para actualizar una categoría

    // Endpoint para actualizar una categoría
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Integer id,
        @Valid @ModelAttribute CategoryRequest categoriesRequest,
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
            // Mapear request -> DTO y delegar la actualización
            CategoriesDTO dto = categoriesMapper.fromRequest(categoriesRequest);
            CategoriesDTO updatedCategory = categoriesService.updateCategory(id, dto, image);

            if (updatedCategory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            CategoryResponse resp = categoriesMapper.toResponse(updatedCategory, serverUrl);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (com.techmate.techmate.exception.BusinessException e) {
            return new ResponseEntity<>(new ErrorResponse(List.of(e.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        try {
        List<CategoryResponse> categories = categoriesService.getAllCategories().stream()
            .map(category -> categoriesMapper.toResponse(category, serverUrl))
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
        } catch (Exception e) {
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
        } catch (com.techmate.techmate.exception.NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Imagen no encontrada
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }
}
