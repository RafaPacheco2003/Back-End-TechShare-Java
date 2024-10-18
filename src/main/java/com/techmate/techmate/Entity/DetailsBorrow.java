package com.techmate.techmate.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class DetailsBorrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer detailsBorrowId; // Identificador del detalle del préstamo

    private Integer quantity;          // Cantidad de materiales

    private double unitPrice;         // Precio unitario del material

    private double totalPrice;        // Precio total del detalle

    @ManyToOne
    @JoinColumn(name = "materials_id")
    private Materials materials; // Material asociado a este detalle

    @ManyToOne
    @JoinColumn(name = "borrow_id")
    private Borrow borrow;     // Préstamo asociado a este detalle
}
