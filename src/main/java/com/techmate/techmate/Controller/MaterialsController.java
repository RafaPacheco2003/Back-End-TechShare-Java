package com.techmate.techmate.Controller;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Service.MaterialsService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@CrossOrigin(origins = "http://localhost:5173") // Permitir solicitudes desde tu frontend
@RestController
@RequestMapping("/admin/materials")
public class MaterialsController {

    @Autowired
    private MaterialsService materialsService;

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    @Autowired
    private ImageStorageStrategy imageStorageStrategy;

    @PostMapping("/create")
    public ResponseEntity<MaterialsDTO> createMaterials(
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("subCategoryId") int subCategoryId,
            @RequestParam("roleId") int roleId) {

        // Guardamos la imagen usando la estrategia de almacenamiento
        String imagePath = imageStorageStrategy.saveImage(image);

        // Crear un nuevo DTO de Materials
        MaterialsDTO materialsDTO = new MaterialsDTO();
        materialsDTO.setImagePath(imagePath);
        materialsDTO.setName(name);
        materialsDTO.setDescription(description);
        materialsDTO.setPrice(price);
        materialsDTO.setSubCategoryId(subCategoryId);
        materialsDTO.setRolId(roleId);

        // Llamar al servicio para guardar el material
        MaterialsDTO createdMaterial = materialsService.createMaterials(materialsDTO);

        return new ResponseEntity<>(createdMaterial, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialsDTO> getMaterialById(@PathVariable("id") Integer id) {
        try {
            MaterialsDTO materialsDTO = materialsService.getMaterialsById(id);
            materialsDTO.setImagePath(serverUrl + "/admin/materials/images/" + materialsDTO.getImagePath());
            return new ResponseEntity<>(materialsDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<MaterialsDTO> updateMaterials(
            @PathVariable("id") Integer id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "subCategoryId", required = false) Integer subCategoryId,
            @RequestParam(value = "roleId", required = false) Integer roleId) {

        MaterialsDTO materialsDTO = new MaterialsDTO();

        if (name != null) materialsDTO.setName(name);
        if (description != null) materialsDTO.setDescription(description);
        if (price != null && price > 0) materialsDTO.setPrice(price);
        if (subCategoryId != null && subCategoryId > 0) materialsDTO.setSubCategoryId(subCategoryId);
        if (roleId != null && roleId > 0) materialsDTO.setRolId(roleId);

        if (image != null && !image.isEmpty()) {
            String imagePath = imageStorageStrategy.saveImage(image);
            materialsDTO.setImagePath(imagePath);
        }

        MaterialsDTO updatedMaterial = materialsService.updateMaterials(id, materialsDTO);

        if (updatedMaterial.getImagePath() != null) {
            updatedMaterial.setImagePath(serverUrl + "/admin/materials/images/" + updatedMaterial.getImagePath());
        }

        return new ResponseEntity<>(updatedMaterial, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MaterialsDTO>> getAllMaterials() {
        List<MaterialsDTO> materialsDTO = materialsService.getAllMaterials();

        if (materialsDTO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        materialsDTO.forEach(material -> {
            if (material.getImagePath() != null) {
                material.setImagePath(serverUrl + "/admin/materials/images/" + material.getImagePath());
            }
        });

        return new ResponseEntity<>(materialsDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMaterials(@PathVariable("id") Integer id) {
        try {
            materialsService.deleteMaterials(id);
            return new ResponseEntity<>("Material eliminado con éxito", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get(storageLocation).resolve(filename);
        File file = imagePath.toFile();

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath))
                .body(imageBytes);
    }
}
