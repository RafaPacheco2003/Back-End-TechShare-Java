package com.techmate.techmate.Entity;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "movements_id")
    private int movements_id;

    private MoveType moveType;

    private String comment;

    private int quantity;

    @Temporal(TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "materials_id")
    private Materials materials;

    // Getters y setters
}
