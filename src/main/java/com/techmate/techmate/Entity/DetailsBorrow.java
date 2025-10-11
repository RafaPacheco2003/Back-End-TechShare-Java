package com.techmate.techmate.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "details_borrow")
@Data
public class DetailsBorrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer detailsBorrowId;  // Auto-mapea a details_borrow_id

    private Integer quantity;  // Auto-mapea a quantity

    private double unitPrice;  // Auto-mapea a unit_price

    private double totalPrice;  // Auto-mapea a total_price

    @ManyToOne
    @JoinColumn(name = "materials_id")
    private Materials materials; // Material asociado a este detalle

    @ManyToOne
    @JoinColumn(name = "borrow_id")
    private Borrow borrow;     // Préstamo asociado a este detalle
}
