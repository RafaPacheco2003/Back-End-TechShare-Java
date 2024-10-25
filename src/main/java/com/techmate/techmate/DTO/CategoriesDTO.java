package com.techmate.techmate.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * La clase {@code CategoriesDTO} es un objeto de transferencia de datos (DTO)
 * que representa una categoría. Se utiliza para encapsular los datos de la
 * entidad {@code Categories} y facilitar la transferencia entre las capas
 * de la aplicación.
 * 
 * <p>Esta clase incluye los siguientes atributos:</p>
 * <ul>
 *     <li><b>categoryId:</b> Identificador único de la categoría.</li>
 *     <li><b>name:</b> Nombre de la categoría.</li>
 *     <li><b>imagePath:</b> Ruta de la imagen asociada a la categoría.</li>
 * </ul>
 */
public class CategoriesDTO {
    
    private int categoryId;
    
    @NotBlank(message = "El nombre de la categoría no puede estar vacío.")
    @Size(min = 3, max = 100, message = "El nombre de la categoría debe tener entre 3 y 100 caracteres.")
    private String name;
    
    
    private String imagePath;

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
     * Constructor vacío que crea una nueva instancia de {@code CategoriesDTO}.
     */
    public CategoriesDTO() {
    }

    /**
     * Constructor que inicializa los atributos de la categoría.
     * 
     * @param categoryId El identificador único de la categoría.
     * @param name El nombre de la categoría.
     * @param imagePath La ruta de la imagen asociada a la categoría.
     */
    public CategoriesDTO(int categoryId, String name, String imagePath) {
        this.categoryId = categoryId;
        this.name = name;
        this.imagePath = imagePath;
    }
}
