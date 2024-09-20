package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.CategoriesDTO;
import com.techmate.techmate.Entity.Categories;
import com.techmate.techmate.Repository.CategoriesRepository;
import com.techmate.techmate.Service.CategoriesService;

/**
 * La clase {@code CategoriesServiceImp} es la implementación de la interfaz
 * {@code CategoriesService}. Proporciona métodos para manejar operaciones
 * relacionadas con las categorías en el sistema, incluyendo la creación,
 * recuperación, actualización y eliminación de categorías.
 * 
 * <p>Esta clase utiliza {@code CategoriesRepository} para acceder a los
 * datos de las categorías en la base de datos y realiza la conversión entre
 * entidades y objetos de transferencia de datos (DTO).</p>
 */
@Service
public class CategoriesServiceImp implements CategoriesService {

    @Autowired
    CategoriesRepository categoriesRepository;

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
    public CategoriesDTO createCategory(CategoriesDTO categoryDTO) {
        Categories categories = convertToEntity(categoryDTO);
        categories = categoriesRepository.save(categories);
        return convertToDTO(categories);
    }

    @Override
    public CategoriesDTO getCategoryById(int categoryID) {
        Categories categories = categoriesRepository.findById(categoryID).orElse(null);
        return categories != null ? convertToDTO(categories) : null;
    }

    @Override
    public CategoriesDTO updateCategory(int categoryID, CategoriesDTO categoryDTO) {
        Categories categories = categoriesRepository.findById(categoryID).orElse(null);

        if (categories != null) {
            categories.setName(categoryDTO.getName());
            categories.setImagePath(categoryDTO.getImagePath());
            categories = categoriesRepository.save(categories);
            return convertToDTO(categories);
        }

        return null;
    }

    @Override
    public void deleteCategory(int categoryID) {
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
