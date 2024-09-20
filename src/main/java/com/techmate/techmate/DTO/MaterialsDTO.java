package com.techmate.techmate.DTO;

public class MaterialsDTO {
    
    private int materialsId;
    private String name;
    private String descrption;
    private double price;
    private int stock;
    private int borrowable_stock;

    private int subCategoryId;
    private String subCategoryName;


    public MaterialsDTO() {
    }


    public MaterialsDTO(int materialsId, String name, String descrption, double price, int stock, int borrowable_stock,
            int subCategoryId, String subCategoryName) {
        this.materialsId = materialsId;
        this.name = name;
        this.descrption = descrption;
        this.price = price;
        this.stock = stock;
        this.borrowable_stock = borrowable_stock;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
    }


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


    public String getDescrption() {
        return descrption;
    }


    public void setDescrption(String descrption) {
        this.descrption = descrption;
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
