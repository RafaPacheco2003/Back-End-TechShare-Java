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
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "materials")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "materials_id")
    private int materialsId;

    @Column(name = "imagePath") 
    private String imagePath;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private double price;

    @Column(name = "stock")
    private int stock;


    @Column(name = "borrowable_stock")
    private int borrowable_stock;

    @ManyToOne
    @JoinColumn(name = "subCategory_id")
    private SubCategories subCategory;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Role role;
}
