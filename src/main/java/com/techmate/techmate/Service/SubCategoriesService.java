package com.techmate.techmate.Service;

import java.util.List;

import com.techmate.techmate.DTO.SubCategoriesDTO;

public interface SubCategoriesService {
    

    SubCategoriesDTO createSubCategory (SubCategoriesDTO subCategoryDTO);
    SubCategoriesDTO getSubCategoryById(int subCategoryID);
    SubCategoriesDTO updateSubCategory(int subCategoryID, SubCategoriesDTO subCategoryDTO);
    void deleteSubCategory(int subCategoryID);
    List<SubCategoriesDTO> getAllSubCategories();


    String getSubCategoryNameById(int subCategoryID);
}
