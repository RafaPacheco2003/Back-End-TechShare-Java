package com.techmate.techmate.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubCategoriesDTO {

    private int subCategoriesId;

    //@NotBlank(message = "El nombre no puede estar vacío") // Valida que el nombre no esté vacío
    //@Size(min = 3, max = 100, message = "El nombre de la subcategoría debe tener entre 3 y 100 caracteres.")
    private String name;
   
    
    private String imagePath;
    
    private int categoryId; // ID de la categoría a la que pertenece la subcategoría
    private String categoryName; // Nuevo campo para el nombre de la categoría

    // Constructores
   

}
