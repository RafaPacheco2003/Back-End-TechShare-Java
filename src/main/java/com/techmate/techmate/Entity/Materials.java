package com.techmate.techmate.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "materials")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
// TEMPORAL: Deshabilitado hasta que migraciones añadan columnas
// public class Materials extends AuditableEntity {
public class Materials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int materialsId;  // Auto-mapea a materials_id

    private String imagePath;  // Auto-mapea a image_path

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;

    @NotNull(message = "El precio no puede ser nulo")
    private double price; // Permite que el precio sea 0

    @NotNull(message = "El stock no puede ser nulo")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0") // Permite que el stock sea 0
    private int stock;

    @NotNull(message = "El stock prestable no puede ser nulo")
    @Min(value = 0, message = "El stock prestable debe ser mayor o igual a 0") // Permite que el stock prestable sea 0
    private int borrowable_stock;  // Ya está en snake_case, auto-mapea directo

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategories subCategory;

    @OneToMany(mappedBy = "materials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleMaterials> roleMaterials;


    @OneToMany(mappedBy = "materials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Movements> movements;

    @OneToMany(mappedBy = "materials", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsBorrow> detailsBorrow; // Relación con los detalles de préstamo
}
