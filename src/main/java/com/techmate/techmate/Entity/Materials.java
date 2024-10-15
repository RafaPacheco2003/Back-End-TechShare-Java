package com.techmate.techmate.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Column(name = "description")
    private String description;

    @NotNull(message = "El precio no puede ser nulo")
    @Column(name = "price")
    private double price; // Permite que el precio sea 0

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0") // Permite que el stock sea 0
    @Column(name = "stock")
    private int stock;

    @NotNull(message = "El stock prestable no puede ser nulo")
    @Min(value = 0, message = "El stock prestable debe ser mayor o igual a 0") // Permite que el stock prestable sea 0
    @Column(name = "borrowable_stock")
    private int borrowable_stock;

    @ManyToOne
    @JoinColumn(name = "subCategory_id")
    private SubCategories subCategory;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Role role;
}
