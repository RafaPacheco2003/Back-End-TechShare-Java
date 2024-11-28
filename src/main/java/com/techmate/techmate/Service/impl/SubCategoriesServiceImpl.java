package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.SubCategoriesDTO;
import com.techmate.techmate.Entity.Categories;
import com.techmate.techmate.Entity.SubCategories;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Repository.CategoriesRepository;
import com.techmate.techmate.Repository.SubCategoriesRepository;
import com.techmate.techmate.Service.CategoriesService;
import com.techmate.techmate.Service.SubCategoriesService;
import com.techmate.techmate.Validation.ImageValidationStrategy;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SubCategoriesServiceImpl implements SubCategoriesService {

    @Autowired
    private SubCategoriesRepository subCategoriesRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
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

    // Método para convertir de entidad a DTO
    private SubCategoriesDTO convertToDTO(SubCategories subCategory) {
        SubCategoriesDTO dto = new SubCategoriesDTO();
        dto.setSubCategoriesId(subCategory.getSubCategoryId());
        dto.setName(subCategory.getName());
        dto.setImagePath(subCategory.getImagePath());

        dto.setCategoryId(subCategory.getCategory().getCategoryId());
        dto.setCategoryName(categoriesService.getCategoryNameById(subCategory.getCategory().getCategoryId())); // Llama
                                                                                                               // al
                                                                                                               // nuevo
                                                                                                               // método
        return dto;
    }

    // Método para convertir de DTO a entidad
    private SubCategories convertToEntity(SubCategoriesDTO subCategoryDTO) {
        SubCategories subCategory = new SubCategories();
        subCategory.setName(subCategoryDTO.getName());
        subCategory.setImagePath(subCategoryDTO.getImagePath());

        // Buscar la categoría por ID y asignarla
        Categories category = categoriesRepository.findById(subCategoryDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Categoría no encontrada con ID: " + subCategoryDTO.getCategoryId()));
        subCategory.setCategory(category);

        return subCategory;
    }

    @Override
    public SubCategoriesDTO createSubCategory(SubCategoriesDTO subCategoryDTO, MultipartFile image) {
        
        if (subCategoriesRepository.findByName(subCategoryDTO.getName()) != null) {
            throw new IllegalArgumentException("Ya existe una subCategoria con el nombre: " + subCategoryDTO.getName());
            
        }

        String imagePath = image.getOriginalFilename(); // Obtener el nombre original de la imagen
        imageValidationStrategy.validate(imagePath); // Validación de la extensión

        // Guardar la imagen y obtener la ruta
        String savedImagePath = imageStorageStrategy.saveImage(image); // Asegúrate de que este método acepte

        subCategoryDTO.setImagePath(savedImagePath);

        SubCategories subCategory = convertToEntity(subCategoryDTO);
        subCategory = subCategoriesRepository.save(subCategory);
        return convertToDTO(subCategory);
    }

    @Override
    public SubCategoriesDTO getSubCategoryById(int subCategoryID) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + subCategoryID));
        return subCategory != null ? convertToDTO(subCategory) : null;
    }

    @Override
    public SubCategoriesDTO updateSubCategory(int subCategoryID, SubCategoriesDTO subCategoryDTO, MultipartFile image) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + subCategoryID));
        
                if (subCategoryDTO.getName() != null &&
                    !subCategoryDTO.getName().equals(subCategory.getName()) &&
                    subCategoriesRepository.findByName(subCategoryDTO.getName()) != null) {
            throw new IllegalArgumentException("Ya existe una subCategoria con el nombre: " + subCategoryDTO.getName());
            
        }
        if (subCategory != null) {
            subCategory.setName(subCategoryDTO.getName());
            subCategory.setImagePath(subCategoryDTO.getImagePath());
            // Buscar y asignar la categoría actualizada si se proporciona un nuevo ID de
            // categoría
            Categories category = categoriesRepository.findById(subCategoryDTO.getCategoryId()).orElse(null);
            subCategory.setCategory(category);


            if (image != null && !image.isEmpty()) {
                String oldImagePath = subCategory.getImagePath();

                // Eliminar la imagen antigua si existe
                if (oldImagePath != null && !oldImagePath.isEmpty()) {
                    imageStorageStrategy.deleteImage(oldImagePath);
                }

                // Validar la nueva imagen usando la estrategia de validación
                String newImagePath = image.getOriginalFilename();
                imageValidationStrategy.validate(newImagePath); // Validar extensión o formato

                // Guardar la nueva imagen y establecer su ruta en la entidad
                newImagePath = imageStorageStrategy.saveImage(image);
                subCategory.setImagePath(newImagePath); // Actualizar la ruta de la imagen
            }

            subCategory = subCategoriesRepository.save(subCategory);
            return convertToDTO(subCategory);
        }

        return null;
    }

    @Override
    public void deleteSubCategory(int subCategoryID) {
        // Buscar la subcategoría por ID y lanzar excepción si no se encuentra
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID)
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found with id: " + subCategoryID));

        // Eliminar la imagen si existe
        String imagePath = subCategory.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia para eliminar la imagen
        }

        // Eliminar la subcategoría de la base de datos
        subCategoriesRepository.deleteById(subCategoryID);
    }

    @Override
    public List<SubCategoriesDTO> getAllSubCategories() {
        return subCategoriesRepository.findAll().stream() // Obtiene una lista de SubCategories y la convierte en un
                                                          // stream.
                .map(this::convertToDTO) // Transforma cada SubCategory en un SubCategoriesDTO usando convertToDTO.
                .collect(Collectors.toList()); // Reúne todos los SubCategoriesDTO en una nueva lista y la devuelve.
    }

    @Override
    public String getSubCategoryNameById(int subCategoryID) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID).orElse(null);

        return subCategory != null ? subCategory.getName() : null;
    }
}
