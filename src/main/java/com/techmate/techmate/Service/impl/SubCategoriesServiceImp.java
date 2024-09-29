package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.SubCategoriesDTO;
import com.techmate.techmate.Entity.Categories;
import com.techmate.techmate.Entity.SubCategories;
import com.techmate.techmate.Repository.CategoriesRepository;
import com.techmate.techmate.Repository.SubCategoriesRepository;
import com.techmate.techmate.Service.CategoriesService;
import com.techmate.techmate.Service.SubCategoriesService;

@Service
public class SubCategoriesServiceImp implements SubCategoriesService {

    @Autowired
    private SubCategoriesRepository subCategoriesRepository;
    
    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private CategoriesService categoriesService;

    // Método para convertir de entidad a DTO
    private SubCategoriesDTO convertToDTO(SubCategories subCategory) {
        SubCategoriesDTO dto = new SubCategoriesDTO();
        dto.setSubCategoriesId(subCategory.getSubCategoryId());
        dto.setName(subCategory.getName());
        dto.setImagePath(subCategory.getImagePath());
        
        dto.setCategoryId(subCategory.getCategory().getCategoryId());
        dto.setCategoryName(categoriesService.getCategoryNameById(subCategory.getCategory().getCategoryId())); // Llama al nuevo método
        return dto;
    }
    

    // Método para convertir de DTO a entidad
    private SubCategories convertToEntity(SubCategoriesDTO subCategoryDTO) {
        SubCategories subCategory = new SubCategories();
        subCategory.setName(subCategoryDTO.getName());
        subCategory.setImagePath(subCategoryDTO.getImagePath());

        // Buscar la categoría por ID y asignarla
        Categories category = categoriesRepository.findById(subCategoryDTO.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + subCategoryDTO.getCategoryId()));
        subCategory.setCategory(category);
        
        return subCategory;
    }

    @Override
    public SubCategoriesDTO createSubCategory(SubCategoriesDTO subCategoryDTO) {
        SubCategories subCategory = convertToEntity(subCategoryDTO);
        subCategory = subCategoriesRepository.save(subCategory);
        return convertToDTO(subCategory);
    }

    @Override
    public SubCategoriesDTO getSubCategoryById(int subCategoryID) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID).orElse(null);
        return subCategory != null ? convertToDTO(subCategory) : null;
    }

    @Override
    public SubCategoriesDTO updateSubCategory(int subCategoryID, SubCategoriesDTO subCategoryDTO) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID).orElse(null);

        if (subCategory != null) {
            subCategory.setName(subCategoryDTO.getName());
            subCategory.setImagePath(subCategoryDTO.getImagePath());

            // Buscar y asignar la categoría actualizada si se proporciona un nuevo ID de categoría
            Categories category = categoriesRepository.findById(subCategoryDTO.getCategoryId()).orElse(null);
            subCategory.setCategory(category);

            subCategory = subCategoriesRepository.save(subCategory);
            return convertToDTO(subCategory);
        }

        return null;
    }

    @Override
    public void deleteSubCategory(int subCategoryID) {
        subCategoriesRepository.deleteById(subCategoryID);
    }

    @Override
    public List<SubCategoriesDTO> getAllSubCategories() {
        return subCategoriesRepository.findAll().stream() // Obtiene una lista de SubCategories y la convierte en un stream.
                .map(this::convertToDTO) // Transforma cada SubCategory en un SubCategoriesDTO usando convertToDTO.
                .collect(Collectors.toList()); // Reúne todos los SubCategoriesDTO en una nueva lista y la devuelve.
    }



    @Override
    public String getSubCategoryNameById(int subCategoryID) {
        SubCategories subCategory = subCategoriesRepository.findById(subCategoryID).orElse(null);
        
        return subCategory != null ? subCategory.getName() : null;
    }
}
