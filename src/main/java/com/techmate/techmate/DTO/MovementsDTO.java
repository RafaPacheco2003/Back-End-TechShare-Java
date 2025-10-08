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
    
    @NotNull(message = "Movement type is required")
    private MoveType moveType;
   
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10000, message = "Quantity cannot exceed 10000")
    private int quantity;
    
    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private Date date;
    
    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;

    @Positive(message = "Admin ID must be positive")
    private int adminId;
    
    private String adminName; // Calculado, no necesita validación

    @NotNull(message = "Material ID is required")
    @Positive(message = "Material ID must be positive")
    private int materialsId;
    
    private String materialsName; // Calculado, no necesita validación
}
