package com.techmate.techmate.dto;

import java.util.Date;

import com.techmate.techmate.entity.MoveType;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Movements con validaciones Bean Validation.
 * Mejora la robustez del API validando inputs antes de procesarlos.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementsDTO {
    
    private int movementsId;
    
    @NotNull(message = "El tipo de movimiento es obligatorio")
    private MoveType moveType;
   
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 10000, message = "La cantidad no puede exceder 10000")
    private int quantity;
    
    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private Date date;
    
    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comment;

    @Positive(message = "El ID del administrador debe ser positivo")
    private int adminId;
    
    private String adminName; // Calculado, no necesita validación

    @NotNull(message = "El ID del material es obligatorio")
    @Positive(message = "El ID del material debe ser positivo")
    private int materialsId;
    
    private String materialsName; // Calculado, no necesita validación
}
