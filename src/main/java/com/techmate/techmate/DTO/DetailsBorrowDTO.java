package com.techmate.techmate.DTO;
import lombok.Data;

@Data
public class DetailsBorrowDTO {

    private Integer detailsBorrowId; // Identificador del detalle del préstamo
    private Integer quantity;          // Cantidad de materiales
    private double unitPrice;         // Precio unitario del material
    private double totalPrice;        // Precio total del detalle
    private Integer materialsId;      // ID del material asociado a este detalle
    private Integer borrowId;         // ID del préstamo asociado a este detalle
}
