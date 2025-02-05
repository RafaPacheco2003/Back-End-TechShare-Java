package com.techmate.techmate.DTO;

import java.util.Date;

import com.techmate.techmate.Entity.MoveType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementsDTO {
    
    private int movementsId;
    private MoveType moveType;
   
    private int quantity;
    private Date date;
    private String comment;

    private int adminId;       // Referencia al ID del usuario relacionado
    private String adminName;

    private int materialsId;     // Referencia al ID del material relacionado
    private String materialsName;
}
