package com.techmate.techmate.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementsDTO {
    
    private int movementsId;
    private String moveType;
    private String comment;
    private int quantity;
    private Date date;

    private int usuarioId;       // Referencia al ID del usuario relacionado
    private String usuarioName;

    private int materialsId;     // Referencia al ID del material relacionado
    private String materialsName;
}
