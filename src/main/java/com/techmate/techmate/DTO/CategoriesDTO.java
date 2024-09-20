package com.techmate.techmate.DTO;

public class CategoriesDTO {
    
    private int categoryId;
    
    private String name;
    private String imagePath;

    // Getters y Settersy
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Constructor vacío
    public CategoriesDTO() {
    }

    // Constructor con parámetros
    public CategoriesDTO(int categoryId, String name, String imagePath) {
        this.categoryId = categoryId;
        this.name = name;
        this.imagePath = imagePath;
    }
}
