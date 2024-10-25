package com.techmate.techmate.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "subCategories")
public class SubCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subCategory_id")
    private int subCategoryId;

    @NotBlank(message = "El nombre no puede estar vacío") // Valida que el nombre no esté vacío
    @Column(name = "name")
    private String name;

    @Column(name = "imagePath")
    private String imagePath;

    @NotNull(message = "La categoría no puede ser nula") // Valida que la categoría no sea nula
    @ManyToOne
    @JoinColumn(name = "category_id")  // Clave foránea que apunta a la categoría
    private Categories category;

    @OneToMany(mappedBy = "subCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materials> materials;

}
