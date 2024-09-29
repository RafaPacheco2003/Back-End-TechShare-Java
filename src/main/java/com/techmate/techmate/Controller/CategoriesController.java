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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.CategoriesDTO;
import com.techmate.techmate.Service.CategoriesService;

/**
 * La clase {@code CategoriesController} maneja las solicitudes HTTP relacionadas
 * con las categorías en el sistema.
 * 
 * <p>Proporciona métodos para crear, obtener, actualizar y eliminar categorías,
 * así como para manejar la carga y recuperación de imágenes asociadas.</p>
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173") // Permitir solicitudes desde tu frontend


@RequestMapping("/categories")
public class CategoriesController {

    @Autowired
    private CategoriesService categoriesService;

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor




    /**
     * Obtiene todas las categorías con las rutas completas de las imágenes.
     * 
     * @return Una lista de {@code CategoriesDTO} con todas las categorías y sus imágenes.
     */
    @GetMapping("/all")
    public ResponseEntity<List<CategoriesDTO>> getAllCategories() {
        
        List<CategoriesDTO> categories = categoriesService.getAllCategories().stream()
                .map(category -> {
                    String imagePath = serverUrl + "/categories/images/" + category.getImagePath();
                    category.setImagePath(imagePath);
                    return category;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Crea una nueva categoría y guarda la imagen asociada.
     * 
     * @param name  El nombre de la categoría.
     * @param image El archivo de imagen asociado a la categoría.
     * @return La categoría creada con la ruta completa de la imagen.
     */
    @PostMapping("/create")
    public ResponseEntity<CategoriesDTO> createCategory(
            @RequestParam("name") String name,
            @RequestParam("image") MultipartFile image) {

        if (image.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error si la imagen está vacía
        }
        
        String imagePath = saveImage(image);

        CategoriesDTO categoryDTO = new CategoriesDTO();
        categoryDTO.setName(name);
        categoryDTO.setImagePath(imagePath);

        CategoriesDTO savedCategory = categoriesService.createCategory(categoryDTO);
        savedCategory.setImagePath(serverUrl + "/categories/images/" + savedCategory.getImagePath());

        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    /**
     * Obtiene una categoría por su ID.
     * 
     * @param id El ID de la categoría.
     * @return La categoría correspondiente o un estado NOT_FOUND si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoriesDTO> getCategoryById(@PathVariable("id") Integer id) {
        CategoriesDTO category = categoriesService.getCategoryById(id);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Error si la categoría no se encuentra
        }
        category.setImagePath(serverUrl + "/categories/images/" + category.getImagePath());
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    /**
     * Actualiza una categoría existente. Permite cambiar el nombre y/o la imagen.
     * 
     * @param id    El ID de la categoría.
     * @param name  El nuevo nombre de la categoría (opcional).
     * @param image El nuevo archivo de imagen (opcional).
     * @return La categoría actualizada con la ruta completa de la imagen.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriesDTO> updateCategory(
            @PathVariable("id") Integer id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        CategoriesDTO existingCategory = categoriesService.getCategoryById(id);
        if (existingCategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Error si la categoría no se encuentra
        }

        if (name != null) {
            existingCategory.setName(name);
        }

        if (image != null && !image.isEmpty()) {
            // Eliminar la imagen antigua si existe
            String oldImagePath = existingCategory.getImagePath();
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                Path oldImageFilePath = Paths.get(storageLocation).resolve(oldImagePath);
                File oldImageFile = oldImageFilePath.toFile();
                if (oldImageFile.exists()) {
                    oldImageFile.delete();
                }
            }

            // Guardar la nueva imagen
            String newImagePath = saveImage(image);
            existingCategory.setImagePath(newImagePath);
        }

        CategoriesDTO updatedCategory = categoriesService.updateCategory(id, existingCategory);
        updatedCategory.setImagePath(serverUrl + "/categories/images/" + updatedCategory.getImagePath());

        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    /**
     * Elimina una categoría por su ID y su imagen asociada si existe.
     * 
     * @param id El ID de la categoría.
     * @return Respuesta vacía con código de estado NO_CONTENT.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Integer id) {
        CategoriesDTO category = categoriesService.getCategoryById(id);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Error si la categoría no se encuentra
        }

        // Eliminar la imagen si existe
        String imagePath = category.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Path imageFilePath = Paths.get(storageLocation).resolve(imagePath);
            File file = imageFilePath.toFile();
            if (file.exists()) {
                file.delete();
            }
        }

        categoriesService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Obtiene una imagen por su nombre de archivo.
     * 
     * @param filename El nombre del archivo de la imagen.
     * @return La imagen en formato de bytes o un estado NOT_FOUND si no se encuentra.
     * @throws IOException Si ocurre un error al leer el archivo de la imagen.
     */
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get(storageLocation).resolve(filename);
        File file = imagePath.toFile();

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Error si la imagen no se encuentra
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath))
                .body(imageBytes);
    }

    /**
     * Guarda una imagen en el directorio especificado.
     * 
     * @param image El archivo de imagen a guardar.
     * @return El nombre del archivo guardado.
     * @throws RuntimeException Si ocurre un error al guardar la imagen.
     */
    private String saveImage(MultipartFile image) {
        Path path = Paths.get(storageLocation, image.getOriginalFilename());
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, image.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save image", e);
        }
        return image.getOriginalFilename(); // Devuelve solo el nombre del archivo
    }
}
