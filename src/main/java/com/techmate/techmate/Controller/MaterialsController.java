package com.techmate.techmate.Controller;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Service.EmailService;
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


@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
@RestController
@RequestMapping("/admin/materials")
public class MaterialsController {

    @Autowired
    private MaterialsService materialsService;

    @Autowired
    private EmailService emailService;

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
            @RequestParam("roleIds") List<Integer> roleIds) { // Cambiado a roleIds como lista

        try {
            // Crear un nuevo DTO de Materials
            MaterialsDTO materialsDTO = new MaterialsDTO();
            materialsDTO.setImagePath(image.getOriginalFilename()); // Obtener el nombre original de la imagen
            materialsDTO.setName(name);
            materialsDTO.setDescription(description);
            materialsDTO.setPrice(price);
            materialsDTO.setSubCategoryId(subCategoryId);
            materialsDTO.setRoleIds(roleIds); // Asignar la lista de roleIds

            // Llamar al servicio para guardar el material
            MaterialsDTO createdMaterial = materialsService.createMaterials(materialsDTO, image);

            return new ResponseEntity<>(createdMaterial, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace(); // Esto te dará la traza del error en la consola
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
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
    public ResponseEntity<?> updateMaterials(
            @PathVariable("id") Integer id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "subCategoryId", required = false) Integer subCategoryId,
            @RequestParam(value = "roleIds", required = false) List<Integer> roleIds) { // Cambiado a roleIds como lista

        MaterialsDTO materialsDTO = new MaterialsDTO();

        materialsDTO.setName(name);
        materialsDTO.setDescription(description);
        materialsDTO.setPrice(price);
        materialsDTO.setSubCategoryId(subCategoryId);
        materialsDTO.setRoleIds(roleIds); // Asignar la lista de roleIds

        MaterialsDTO updatedMaterial = materialsService.updateMaterials(id, materialsDTO, image);

        if (updatedMaterial == null) {
            return new ResponseEntity<>("Error al actualizar un nuevo material", HttpStatus.NOT_FOUND);
        }

        updatedMaterial.setImagePath(serverUrl + "/admin/materials/images/" + updatedMaterial.getImagePath());

        return new ResponseEntity<>(updatedMaterial, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MaterialsDTO>> getAllMaterials() {
        try {
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
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sorted-by-price")
    public ResponseEntity<List<MaterialsDTO>> getAllMaterialsSortedByPrice(@RequestParam(value = "asc", defaultValue = "false") boolean ascending) {
        try {
            List<MaterialsDTO> materialsDTO = materialsService.getAllMaterialsSortedByPrice(ascending);
    
            if (materialsDTO.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
    
            materialsDTO.forEach(material -> {
                if (material.getImagePath() != null) {
                    material.setImagePath(serverUrl + "/admin/materials/images/" + material.getImagePath());
                }
            });
    
            return new ResponseEntity<>(materialsDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Error al obtener materiales
        }
    }
    

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMaterials(@PathVariable("id") Integer id) {
        try {
            MaterialsDTO material = materialsService.getMaterialsById(id); // Verifica si el material existe

            if (material == null) {
                return new ResponseEntity<>("Material no encontrado", HttpStatus.NOT_FOUND);
            }

            materialsService.deleteMaterials(id);
            return new ResponseEntity<>("Material eliminado con éxito", HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/test-email")
    public ResponseEntity<String> testEmail() {
        try {
            emailService.sendEmail("rodrigorafaelchipacheco@gmail.com", "Test", "Este es un mensaje de prueba.");
            return new ResponseEntity<>("Correo enviado", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error enviando correo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
