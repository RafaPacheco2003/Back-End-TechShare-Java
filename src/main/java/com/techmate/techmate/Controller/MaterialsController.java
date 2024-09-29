package com.techmate.techmate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.Service.MaterialsService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/admin/materials")
public class MaterialsController {

    @Autowired
    private MaterialsService materialsService;

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    @PostMapping("/create")
    public ResponseEntity<MaterialsDTO> createMaterials(
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") double price,
            @RequestParam("subCategoryId") int subCategoryId,
            @RequestParam("roleId") int roleId) {

        if (image.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error si la imagen está vacía
        }

        String imagePath = saveImage(image);

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

        // Retornar la respuesta con el material creado y el estado HTTP 201
        return new ResponseEntity<>(createdMaterial, HttpStatus.CREATED);

    }

    /**
     * Obtiene todos los materiales filtrados por el rol del usuario autenticado.
     * 
     * @return Una lista de materiales filtrados por el rol.
     */
    @GetMapping("/by-role")
    public ResponseEntity<List<MaterialsDTO>> getMaterialsByRole() {
        try {
            List<MaterialsDTO> materials = materialsService.getAllMaterialsByRole();
            return ResponseEntity.ok(materials);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // o maneja el error según lo necesites
        }
    }

    /**
     * Obtiene una imagen por su nombre de archivo.
     * 
     * @param filename El nombre del archivo de la imagen.
     * @return La imagen en formato de bytes.
     * @throws IOException Si ocurre un error al leer el archivo de la imagen.
     */
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        // Crea un objeto Path que representa la ruta completa del archivo de imagen
        Path imagePath = Paths.get(storageLocation).resolve(filename);

        // Convierte el objeto Path a un objeto File para poder trabajar con él
        File file = imagePath.toFile();

        // Verifica si el archivo de imagen no existe
        if (!file.exists()) {
            // Si el archivo no se encuentra, devuelve un ResponseEntity con el estado
            // NOT_FOUND (404)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Error si la imagen no se encuentra
        }

        // Lee todos los bytes del archivo de imagen y los almacena en un arreglo de
        // bytes
        byte[] imageBytes = Files.readAllBytes(imagePath);

        // Crea una respuesta HTTP con estado OK (200) y agrega el tipo de contenido de
        // la imagen
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(imagePath)) // Establece el tipo de contenido
                .body(imageBytes); // Establece el cuerpo de la respuesta con los bytes de la imagen
    }

    /**
     * Guardamos una imagen en el directorio especifico
     * 
     * @param image El archivo de imagen a guardar.
     * @return El nombre de la imagen guardado.
     */
    private String saveImage(MultipartFile image) {

        // Crea un objeto Path que representa la ruta donde se guardará la imagen
        Path path = Paths.get(storageLocation, image.getOriginalFilename());

        try {
            // Crea los directorios necesarios para la ruta del archivo, si no existen
            Files.createDirectories(path.getParent());

            // Escribe los bytes de la imagen en el archivo en la ruta especificada
            Files.write(path, image.getBytes());

        } catch (Exception e) {
            // Si ocurre un error al guardar la imagen, lanza una RuntimeException con un
            // mensaje de error
            throw new RuntimeException("Failed to save image", e);
        }
        // Devuelve solo el nombre del archivo original de la imagen
        return image.getOriginalFilename();
    }

}
