package com.techmate.techmate.Controller;

// image storage handled by service
import java.util.stream.Collectors;
import com.techmate.techmate.Service.EmailService;
import com.techmate.techmate.Service.MaterialsService;
import com.techmate.techmate.Service.materials.mapper.MaterialsMapper;
import com.techmate.techmate.dto.MaterialRequest;
import com.techmate.techmate.dto.MaterialResponse;
import com.techmate.techmate.dto.MaterialsDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// org.springframework.beans.factory.annotation.Autowired removed (not used)
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

    private final MaterialsService materialsService;
    private final EmailService emailService;
    private final MaterialsMapper materialsMapper;
    private final String storageLocation; // Directorio para almacenar imágenes
    private final String serverUrl; // URL base del servidor
    // image storage strategy is injected but not used directly in this controller
    // image storage handled by service

    public MaterialsController(MaterialsService materialsService, EmailService emailService,
            MaterialsMapper materialsMapper,
            @Value("${storage.location}") String storageLocation, @Value("${server.url}") String serverUrl) {
        this.materialsService = materialsService;
        this.emailService = emailService;
        this.materialsMapper = materialsMapper;
        this.storageLocation = storageLocation;
        this.serverUrl = serverUrl;
    }

    @PostMapping("/create")
    public ResponseEntity<MaterialResponse> createMaterials(
            @RequestParam("image") MultipartFile image,
            @ModelAttribute MaterialRequest materialRequest) {

        try {
        // Mapear request público -> DTO interno usando MaterialsMapper
        MaterialsDTO requestDto = materialsMapper.fromRequest(materialRequest);
        requestDto.setImagePath(image != null ? image.getOriginalFilename() : null);

        MaterialsDTO createdMaterial = materialsService.createMaterials(requestDto, image);

        // Mapear DTO interno -> response público
        MaterialResponse resp = materialsMapper.toResponse(createdMaterial, serverUrl);

            return new ResponseEntity<>(resp, HttpStatus.CREATED);
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
    public ResponseEntity<MaterialResponse> getMaterialById(@PathVariable("id") Integer id) {
        try {
            MaterialsDTO materialsDTO = materialsService.getMaterialsById(id);
        MaterialResponse resp = materialsMapper.toResponse(materialsDTO, serverUrl);
        return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMaterials(
            @PathVariable("id") Integer id,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @ModelAttribute MaterialRequest materialRequest) {

        MaterialsDTO materialsDTO = materialsMapper.fromRequest(materialRequest);
        // image handled by controller when provided
        if (image != null) materialsDTO.setImagePath(image.getOriginalFilename());

        MaterialsDTO updatedMaterial = materialsService.updateMaterials(id, materialsDTO, image);

        if (updatedMaterial == null) {
            return new ResponseEntity<>("Error al actualizar un nuevo material", HttpStatus.NOT_FOUND);
        }

        MaterialResponse resp = materialsMapper.toResponse(updatedMaterial, serverUrl);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<MaterialResponse>> getAllMaterials() {
        try {
            List<MaterialsDTO> materialsDTO = materialsService.getAllMaterials();

            if (materialsDTO.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<MaterialResponse> resp = materialsDTO.stream()
                    .map(material -> materialsMapper.toResponse(material, serverUrl))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(resp, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sorted-by-price")
    public ResponseEntity<List<MaterialResponse>> getAllMaterialsSortedByPrice(
            @RequestParam(value = "asc", defaultValue = "false") boolean ascending) {
        try {
            List<MaterialsDTO> materialsDTO = materialsService.getAllMaterialsSortedByPrice(ascending);

            if (materialsDTO.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<MaterialResponse> resp = materialsDTO.stream()
                    .map(material -> materialsMapper.toResponse(material, serverUrl))
                    .collect(Collectors.toList());

            return new ResponseEntity<>(resp, HttpStatus.OK);
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
