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
    @Column(name = "id")
    private int id;

    // Compatibility getters/setters for legacy code/tests referencing movementsId
    public int getMovementsId() { return this.id; }
    public void setMovementsId(int id) { this.id = id; }

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private MoveType moveType;  // Auto-mapea a type (movement_type)

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "comment")
    private String comment;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Materials materials;

    // Getters y setters
}
