package com.techmate.techmate.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.Service.SubCategoriesService;
import com.techmate.techmate.Service.subcategories.mapper.SubCategoriesMapper;
// ImageStorageStrategy not needed in controller after refactor
import com.techmate.techmate.dto.ErrorResponse;
import com.techmate.techmate.dto.SubCategoriesDTO;
import com.techmate.techmate.dto.SubCategoryRequest;
import com.techmate.techmate.dto.SubCategoryResponse;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
@RequestMapping("admin/subcategories")
public class SubcategoriesController {

    private final SubCategoriesService subcategoriesService;

    private final SubCategoriesMapper subCategoriesMapper;

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    public SubcategoriesController(SubCategoriesService subcategoriesService, SubCategoriesMapper subCategoriesMapper) {
        this.subcategoriesService = subcategoriesService;
        this.subCategoriesMapper = subCategoriesMapper;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSubcategory(
            @Valid @ModelAttribute SubCategoryRequest request,
            @RequestParam("image") MultipartFile image) {

        try {
        SubCategoriesDTO dto = subCategoriesMapper.fromRequest(request);
        SubCategoriesDTO saved = subcategoriesService.createSubCategory(dto, image);
        SubCategoryResponse resp = subCategoriesMapper.toResponse(saved, serverUrl);
        return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (com.techmate.techmate.exception.NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (com.techmate.techmate.exception.BusinessException e) {
            return new ResponseEntity<>(new ErrorResponse(List.of(e.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSubcategoryById(@PathVariable("id") Integer id) {
        try {
            SubCategoriesDTO subcategory = subcategoriesService.getSubCategoryById(id);

            if (subcategory == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        SubCategoryResponse resp = subCategoriesMapper.toResponse(subcategory, serverUrl);
        return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (com.techmate.techmate.exception.NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener la subcategoría
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSubcategory(
            @PathVariable("id") Integer id,
            @Valid @ModelAttribute SubCategoryRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            SubCategoriesDTO dto = subCategoriesMapper.fromRequest(request);
            SubCategoriesDTO updated = subcategoriesService.updateSubCategory(id, dto, image);

            if (updated == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            SubCategoryResponse resp = subCategoriesMapper.toResponse(updated, serverUrl);
            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (com.techmate.techmate.exception.NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (com.techmate.techmate.exception.BusinessException e) {
            return new ResponseEntity<>(new ErrorResponse(List.of(e.getMessage())), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error general
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSubcategories() {
        try {
        List<SubCategoryResponse> subcategories = subcategoriesService.getAllSubCategories().stream()
            .map(subcategory -> subCategoriesMapper.toResponse(subcategory, serverUrl))
            .collect(Collectors.toList());

            if (subcategories.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT); // No hay subcategorías
            }

            return new ResponseEntity<>(subcategories, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener subcategorías
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable("id") Integer id) {
        try {
            subcategoriesService.deleteSubCategory(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Eliminación exitosa
        } catch (com.techmate.techmate.exception.NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
