package com.techmate.techmate.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "materials")
@AllArgsConstructor
@NoArgsConstructor
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "materials_id")
    private int materialsId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String descrption;

    @Column(name = "price")
    private double price;

    @Column(name = "stock")
    private int stock;

    @Column(name = "borrowable_stock")
    private int borrowable_stock;

    @ManyToOne
    @JoinColumn(name = "subCategory_id")
    private SubCategories subCategory;

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

    public SubCategories getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategories subCategory) {
        this.subCategory = subCategory;
    }
    
    
}
