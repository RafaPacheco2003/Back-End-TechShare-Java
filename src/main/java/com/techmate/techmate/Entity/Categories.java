package com.techmate.techmate.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * La clase {@code Categories} representa una categoría en el sistema.
 * Esta entidad se mapea a la tabla {@code categories} en la base de datos.
 * 
 * <p>La clase incluye los siguientes atributos:</p>
 * <ul>
 *     <li><b>categoryId:</b> Identificador único de la categoría.</li>
 *     <li><b>name:</b> Nombre de la categoría.</li>
 *     <li><b>imagePath:</b> Ruta de la imagen asociada a la categoría.</li>
 *     <li><b>subCategories:</b> Lista de subcategorías asociadas a esta categoría.</li>
 * </ul>
 * 
 * <p>Además, la clase utiliza Lombok para reducir el código boilerplate mediante
 * las anotaciones {@code @AllArgsConstructor} y {@code @NoArgsConstructor}.</p>
 */
@Entity
@Table(name = "categories")
@AllArgsConstructor
@NoArgsConstructor
public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "imagePath") 
    private String imagePath;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubCategories> subCategories;

    

    // Getters y Setters

    /**
     * Obtiene el identificador de la categoría.
     * 
     * @return El identificador de la categoría.
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Establece el identificador de la categoría.
     * 
     * @param categoryId El nuevo identificador de la categoría.
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Obtiene el nombre de la categoría.
     * 
     * @return El nombre de la categoría.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre de la categoría.
     * 
     * @param name El nuevo nombre de la categoría.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la ruta de la imagen de la categoría.
     * 
     * @return La ruta de la imagen.
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Establece la ruta de la imagen de la categoría.
     * 
     * @param imagePath La nueva ruta de la imagen.
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Obtiene la lista de subcategorías asociadas a esta categoría.
     * 
     * @return La lista de subcategorías.
     */
    public List<SubCategories> getSubCategories() {
        return subCategories;
    }

    /**
     * Establece la lista de subcategorías asociadas a esta categoría.
     * 
     * @param subCategories La nueva lista de subcategorías.
     */
    public void setSubCategories(List<SubCategories> subCategories) {
        this.subCategories = subCategories;
    }
}
