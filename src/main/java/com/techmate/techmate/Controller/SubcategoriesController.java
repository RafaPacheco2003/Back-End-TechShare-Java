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

import com.techmate.techmate.DTO.SubCategoriesDTO;
import com.techmate.techmate.Service.SubCategoriesService;

@RestController
@RequestMapping("/subcategories")
public class SubcategoriesController {

    @Autowired
    private SubCategoriesService subcategoriesService;

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    /**
     * Crea una nueva subcategoría y guarda la imagen asociada.
     * 
     * @param name  El nombre de la subcategoría.
     * @param image El archivo de imagen asociado a la subcategoría.
     * @return La subcategoría creada con la ruta completa de la imagen.
     */
    @PostMapping("/create")
    public ResponseEntity<SubCategoriesDTO> createSubcategory(
            @RequestParam("name") String name,
            @RequestParam("image") MultipartFile image,
            @RequestParam("idCategory") Integer idCategoria) {

        if (image.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Error si la imagen está vacía
        }

        // Guarda la imagen
        String imagePath = saveImage(image);

        // Crea el DTO de Subcategoría
        SubCategoriesDTO subcategoryDTO = new SubCategoriesDTO();
        subcategoryDTO.setName(name);
        subcategoryDTO.setImagePath(imagePath);

        // Asocia la subcategoría a una categoría usando el idCategoria
        subcategoryDTO.setCategoryId(idCategoria); // Asegúrate de que el DTO tenga este campo

        // Guarda la subcategoría
        SubCategoriesDTO savedSubcategory = subcategoriesService.createSubCategory(subcategoryDTO);
        savedSubcategory.setImagePath(serverUrl + "/subcategories/images/" + savedSubcategory.getImagePath());

        return new ResponseEntity<>(savedSubcategory, HttpStatus.CREATED);
    }

     /**
     * Obtiene una subcategoría por su ID.
     * 
     * @param id El ID de la subcategoría.
     * @return La subcategoría encontrada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubCategoriesDTO> getSubcategoryById(@PathVariable("id") Integer id) {
        SubCategoriesDTO subcategory = subcategoriesService.getSubCategoryById(id);
        if (subcategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        subcategory.setImagePath(serverUrl + "/subcategories/images/" + subcategory.getImagePath());
        return new ResponseEntity<>(subcategory, HttpStatus.OK);
    }

    /**
     * Actualiza una subcategoría existente. Permite cambiar el nombre y/o la
     * imagen.
     * 
     * @param id    El ID de la subcategoría.
     * @param name  El nuevo nombre de la subcategoría (opcional).
     * @param image El nuevo archivo de imagen (opcional).
     * @return La subcategoría actualizada con la ruta completa de la imagen.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<SubCategoriesDTO> updateSubcategory(
            @PathVariable("id") Integer id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "idCategoria", required = false) Integer idCategoria) {

        SubCategoriesDTO existingSubcategory = subcategoriesService.getSubCategoryById(id);
        if (existingSubcategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (name != null) {
            existingSubcategory.setName(name);
        }

        if (image != null && !image.isEmpty()) {
            String newImagePath = saveImage(image);
            existingSubcategory.setImagePath(newImagePath);
        }

        if (idCategoria != null) {
            existingSubcategory.setCategoryId(idCategoria); // Asegúrate de tener el campo en el DTO
        }

        SubCategoriesDTO updatedSubcategory = subcategoriesService.updateSubCategory(id, existingSubcategory);
        updatedSubcategory.setImagePath(serverUrl + "/subcategories/images/" + updatedSubcategory.getImagePath());

        return new ResponseEntity<>(updatedSubcategory, HttpStatus.OK);
    }



    /**
     * Obtiene todas las subcategorías.
     * 
     * @return Una lista de todas las subcategorías.
     */
    @GetMapping("/all")
    public ResponseEntity<List<SubCategoriesDTO>> getAllSubcategories() {
        List<SubCategoriesDTO> subcategories = subcategoriesService.getAllSubCategories();
        if (subcategories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Si no hay subcategorías, devuelve NO_CONTENT
        }
        // Añadir la URL del servidor a la ruta de la imagen
        subcategories.forEach(subcategory -> {
            subcategory.setImagePath(serverUrl + "/subcategories/images/" + subcategory.getImagePath());
        });
        return new ResponseEntity<>(subcategories, HttpStatus.OK);
    }

   

    /**
     * Elimina una subcategoría por su ID.
     * 
     * @param id El ID de la subcategoría a eliminar.
     * @return Respuesta indicando si la eliminación fue exitosa.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable("id") Integer id) {
        SubCategoriesDTO subcategory = subcategoriesService.getSubCategoryById(id);
        if (subcategory == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Si no se encuentra la subcategoría, devuelve NOT_FOUND
        }
        subcategoriesService.deleteSubCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Si se eliminó correctamente, devuelve NO_CONTENT
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
