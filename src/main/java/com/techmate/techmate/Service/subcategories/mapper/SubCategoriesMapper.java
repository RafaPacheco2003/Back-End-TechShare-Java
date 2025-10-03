package com.techmate.techmate.Service.subcategories.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.dto.SubCategoryRequest;
import com.techmate.techmate.dto.SubCategoryResponse;
import com.techmate.techmate.dto.SubCategoriesDTO;

@Component
public class SubCategoriesMapper {

    public SubCategoriesDTO fromRequest(SubCategoryRequest req) {
        if (req == null) return null;
        SubCategoriesDTO dto = new SubCategoriesDTO();
        dto.setName(req.getName());
        dto.setCategoryId(req.getCategoryId());
        return dto;
    }

    public SubCategoryResponse toResponse(SubCategoriesDTO dto, String serverUrl) {
        if (dto == null) return null;
        String image = dto.getImagePath();
        if (image != null && serverUrl != null && !serverUrl.isEmpty() && !image.startsWith("http")) {
            image = serverUrl + "/admin/subcategories/images/" + image;
        }
        return new SubCategoryResponse(dto.getSubCategoriesId(), dto.getName(), image, dto.getCategoryId(), dto.getCategoryName());
    }
}
