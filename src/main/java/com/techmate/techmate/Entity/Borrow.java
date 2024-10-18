package com.techmate.techmate.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer borrowId; 

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha")
    private Date date;

    @Enumerated(EnumType.STRING)
    private Status status;

    private double amount;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Usuario admin;

    @OneToMany(mappedBy = "borrow", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailsBorrow> details; // Relación con los detalles del préstamo

    // Método para calcular el monto total
    public double calculateTotalAmount() {
        return details.stream()
                .mapToDouble(DetailsBorrow::getTotalPrice)
                .sum();
    }
}
