package com.techmate.techmate.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialsDTO {
    
    private int materialsId;
    private String imagePath;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int borrowable_stock;

    private int subCategoryId;
    private String subCategoryName;

       // Nueva lista para roles
       private List<Integer> roleIds; 
       private List<String> roleNames;




    public int getMaterialsId() {
        return materialsId;
    }


    public void setMaterialsId(int materialsId) {
        this.materialsId = materialsId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


 

    public double getPrice() {
        return price;
    }


    public void setPrice(double price) {
        this.price = price;
    }


    public int getStock() {
        return stock;
    }


    public void setStock(int stock) {
        this.stock = stock;
    }


    public int getBorrowable_stock() {
        return borrowable_stock;
    }


    public void setBorrowable_stock(int borrowable_stock) {
        this.borrowable_stock = borrowable_stock;
    }


    public int getSubCategoryId() {
        return subCategoryId;
    }


    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }


    public String getSubCategoryName() {
        return subCategoryName;
    }


    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    

    
}
