package com.techmate.techmate.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "sub_categories")  // ✅ Normalizado a snake_case
public class SubCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subCategoryId;  // Auto-mapea a sub_category_id

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;  // Auto-mapea a 'name'

    private String imagePath;  // Auto-mapea a 'image_path'

    @NotNull(message = "La categoría no puede ser nula")
    @ManyToOne
    @JoinColumn(name = "category_id")  // Clave foránea que apunta a la categoría
    private Categories category;

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materials> materials;

}
