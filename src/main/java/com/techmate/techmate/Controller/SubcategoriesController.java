package com.techmate.techmate.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.SubCategoriesDTO;
import com.techmate.techmate.Service.SubCategoriesService;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Validation.ImageValidationStrategy;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
@RequestMapping("/subcategories")
public class SubcategoriesController {

    @Autowired
    private SubCategoriesService subcategoriesService;

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    @Autowired
    private ImageStorageStrategy imageStorageStrategy; // Estrategia de almacenamiento de imágenes

    @Autowired
    private ImageValidationStrategy imageValidationStrategy; // Estrategia de validación de imágenes

    @PostMapping("/create")
    public ResponseEntity<SubCategoriesDTO> createSubcategory(
            @RequestParam("name") @Valid String name,
            @RequestParam("image") MultipartFile image,
            @RequestParam("idCategory")@Valid  Integer idCategoria) {

        try {
            // Validar la imagen
            imageValidationStrategy.validate(image.getOriginalFilename());

            String imagePath = imageStorageStrategy.saveImage(image);

            SubCategoriesDTO subcategoryDTO = new SubCategoriesDTO();
            subcategoryDTO.setName(name);
            subcategoryDTO.setImagePath(imagePath);
            subcategoryDTO.setCategoryId(idCategoria); // Asocia la subcategoría a una categoría

            SubCategoriesDTO savedSubcategory = subcategoriesService.createSubCategory(subcategoryDTO);
            savedSubcategory.setImagePath(serverUrl + "/subcategories/images/" + savedSubcategory.getImagePath());

            return new ResponseEntity<>(savedSubcategory, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error de validación
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error al guardar la imagen o subcategoría
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategoriesDTO> getSubcategoryById(@PathVariable("id") Integer id) {
        try {
            SubCategoriesDTO subcategory = subcategoriesService.getSubCategoryById(id);
            if (subcategory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            subcategory.setImagePath(serverUrl + "/subcategories/images/" + subcategory.getImagePath());
            return new ResponseEntity<>(subcategory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener la subcategoría
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SubCategoriesDTO> updateSubcategory(
            @PathVariable("id") Integer id,
            @RequestParam(value = "name", required = false) @Valid String name,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "idCategoria", required = false) @Valid Integer idCategoria) {

        try {
            SubCategoriesDTO existingSubcategory = subcategoriesService.getSubCategoryById(id);
            if (existingSubcategory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }


                existingSubcategory.setName(name);


            if (image != null && !image.isEmpty()) {
                // Validar la nueva imagen
                imageValidationStrategy.validate(image.getOriginalFilename());

                // Guardar la nueva imagen
                String newImagePath = imageStorageStrategy.saveImage(image);
                existingSubcategory.setImagePath(newImagePath);
            }

                existingSubcategory.setCategoryId(idCategoria); // Actualiza la categoría asociada
  

            SubCategoriesDTO updatedSubcategory = subcategoriesService.updateSubCategory(id, existingSubcategory);
            updatedSubcategory.setImagePath(serverUrl + "/subcategories/images/" + updatedSubcategory.getImagePath());

            return new ResponseEntity<>(updatedSubcategory, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error de validación
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error en la actualización
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<SubCategoriesDTO>> getAllSubcategories() {
        try {
            List<SubCategoriesDTO> subcategories = subcategoriesService.getAllSubCategories();
            if (subcategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No hay subcategorías
            }
            subcategories.forEach(subcategory -> {
                subcategory.setImagePath(serverUrl + "/subcategories/images/" + subcategory.getImagePath());
            });
            return new ResponseEntity<>(subcategories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener subcategorías
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable("id") Integer id) {
        try {
            SubCategoriesDTO subcategory = subcategoriesService.getSubCategoryById(id);
            if (subcategory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Subcategoría no encontrada
            }

            // Eliminar la imagen si existe
            String imagePath = subcategory.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia de eliminación
            }

            subcategoriesService.deleteSubCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Eliminación exitosa
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al eliminar la subcategoría
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(storageLocation).resolve(filename);
            File file = imagePath.toFile();

            if (!file.exists()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Imagen no encontrada
            }

            byte[] imageBytes = Files.readAllBytes(imagePath);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath))
                    .body(imageBytes);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al leer la imagen
        }
    }
}
