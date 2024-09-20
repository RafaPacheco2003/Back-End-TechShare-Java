package com.techmate.techmate.Service;

import java.util.List;

import com.techmate.techmate.DTO.CategoriesDTO;

public interface CategoriesService {
    
    CategoriesDTO createCategory(CategoriesDTO categoryDTO);

    CategoriesDTO getCategoryById(int categoryID);

    CategoriesDTO updateCategory(int categoryID, CategoriesDTO categoryDTO);

    void deleteCategory(int categoryID);
    
    List<CategoriesDTO> getAllCategories();



    public String getCategoryNameById(int categoryId);

}
