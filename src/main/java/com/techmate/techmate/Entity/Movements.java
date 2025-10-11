package com.techmate.techmate.entity;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
// otros imports
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movements {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int movementsId;  // Auto-mapea a movements_id

    @Enumerated(EnumType.STRING)
    private MoveType moveType;  // Auto-mapea a move_type

    private int quantity;  // Auto-mapea a quantity

    private String comment;  // Auto-mapea a comment

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;  // Auto-mapea a date

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "materials_id")
    private Materials materials;

    // Getters y setters
}
