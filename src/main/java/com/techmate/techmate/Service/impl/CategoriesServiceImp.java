package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.CategoriesDTO;
import com.techmate.techmate.Entity.Categories;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Repository.CategoriesRepository;
import com.techmate.techmate.Service.CategoriesService;
import com.techmate.techmate.Validation.ImageValidationStrategy;

import jakarta.persistence.EntityNotFoundException;

/**
 * La clase {@code CategoriesServiceImp} es la implementación de la interfaz
 * {@code CategoriesService}. Proporciona métodos para manejar operaciones
 * relacionadas con las categorías en el sistema, incluyendo la creación,
 * recuperación, actualización y eliminación de categorías.
 * 
 * <p>
 * Esta clase utiliza {@code CategoriesRepository} para acceder a los
 * datos de las categorías en la base de datos y realiza la conversión entre
 * entidades y objetos de transferencia de datos (DTO).
 * </p>
 */
@Service
public class CategoriesServiceImp implements CategoriesService {

    @Autowired
    CategoriesRepository categoriesRepository;

    @Autowired
    private ImageStorageStrategy imageStorageStrategy;

    @Autowired
    private ImageValidationStrategy imageValidationStrategy; // Inyección de la estrategia de validación

    @Value("${storage.location}")
    private String storageLocation; // Directorio para almacenar imágenes

    @Value("${server.url}")
    private String serverUrl; // URL base del servidor

    /**
     * Convierte una entidad {@code Categories} a un objeto {@code CategoriesDTO}.
     * 
     * @param category La entidad de categoría a convertir.
     * @return Un objeto {@code CategoriesDTO} que representa la categoría.
     */
    private CategoriesDTO convertToDTO(Categories category) {
        CategoriesDTO dto = new CategoriesDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setName(category.getName());
        dto.setImagePath(category.getImagePath());

        return dto;
    }

    /**
     * Convierte un objeto {@code CategoriesDTO} a una entidad {@code Categories}.
     * 
     * @param categoriesDTO El objeto DTO a convertir.
     * @return La entidad {@code Categories} correspondiente.
     */
    private Categories convertToEntity(CategoriesDTO categoriesDTO) {
        Categories categories = new Categories();
        categories.setCategoryId(categoriesDTO.getCategoryId());
        categories.setName(categoriesDTO.getName());
        categories.setImagePath(categoriesDTO.getImagePath());
        return categories;
    }

    @Override
    public CategoriesDTO createCategory(CategoriesDTO categoriesDTO, MultipartFile image) {
        // Verificar si ya existe una categoría con el mismo nombre
        if (categoriesRepository.findByName(categoriesDTO.getName()) != null) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriesDTO.getName());
        }
        String imagePath = image.getOriginalFilename(); // Obtener el nombre original de la imagen
        imageValidationStrategy.validate(imagePath); // Validación de la extensión

        // Guardar la imagen y obtener la ruta
        String savedImagePath = imageStorageStrategy.saveImage(image); // Asegúrate de que este método acepte

        categoriesDTO.setImagePath(savedImagePath);

        // Establecer la ruta de la imagen en el DTO
        categoriesDTO.setImagePath(savedImagePath);

        // Convertir el DTO a entidad y guardarlo en la base de datos
        Categories categories = convertToEntity(categoriesDTO);
        categories = categoriesRepository.save(categories);

        return convertToDTO(categories);
    }

    @Override
    public CategoriesDTO getCategoryById(int categoryID) {
        // Buscar la categoría por ID y lanzar excepción si no se encuentra
        Categories categories = categoriesRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryID));

        return categories != null ? convertToDTO(categories) : null;
    }

    @Override
    public CategoriesDTO updateCategory(int categoryID, CategoriesDTO categoriesDTO, MultipartFile image) {
        // Buscar la categoría por ID y lanzar excepción si no se encuentra
        Categories categories = categoriesRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryID));

        // Verificar si ya existe otra categoría con el mismo nombre y el nombre ha
        // cambiado
        if (categoriesDTO.getName() != null &&
                !categoriesDTO.getName().equals(categories.getName()) &&
                categoriesRepository.findByName(categoriesDTO.getName()) != null) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriesDTO.getName());
        }

        // Actualizar el nombre solo si es diferente y no es nulo
        if (categoriesDTO.getName() != null && !categoriesDTO.getName().isEmpty()) {
            categories.setName(categoriesDTO.getName());
        }

        // Si se proporciona una imagen, validar y guardar la nueva imagen
        if (image != null && !image.isEmpty()) {
            String oldImagePath = categories.getImagePath();

            // Eliminar la imagen antigua si existe
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                imageStorageStrategy.deleteImage(oldImagePath);
            }

            // Validar la nueva imagen usando la estrategia de validación
            String newImagePath = image.getOriginalFilename();
            imageValidationStrategy.validate(newImagePath); // Validar extensión o formato

            // Guardar la nueva imagen y establecer su ruta en la entidad
            newImagePath = imageStorageStrategy.saveImage(image);
            categories.setImagePath(newImagePath); // Actualizar la ruta de la imagen
        }

        // Guardar la entidad actualizada en la base de datos
        categories = categoriesRepository.save(categories);
        return convertToDTO(categories); // Devolver la categoría actualizada
    }

    @Override
    public void deleteCategory(int categoryID) {
        // Buscar la categoría por ID
        Categories category = categoriesRepository.findById(categoryID)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryID));

        // Si se encuentra la categoría, eliminar la imagen si existe
        String imagePath = category.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia para eliminar la imagen
        }

        // Eliminar la categoría de la base de datos
        categoriesRepository.deleteById(categoryID);
    }

    @Override
    public List<CategoriesDTO> getAllCategories() {
        return categoriesRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getCategoryNameById(int categoryId) {
        Categories category = categoriesRepository.findById(categoryId).orElse(null);
        return category != null ? category.getName() : null;
    }
}
