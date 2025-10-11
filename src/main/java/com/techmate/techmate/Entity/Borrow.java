package com.techmate.techmate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "borrow")
@Data
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer borrowId;  // Auto-mapea a borrow_id

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha")  // Mantener: columna NO sigue convención (debería ser 'date')
    private Date date;

    @Temporal(TemporalType.DATE)
    private Date startDate;  // Auto-mapea a start_date

    @Temporal(TemporalType.DATE)
    private Date endDate;  // Auto-mapea a end_date

    @Temporal(TemporalType.DATE)
    private Date returnDate;  // Auto-mapea a return_date

    @Enumerated(EnumType.STRING)
    private Status status;

    private double amount;  // Auto-mapea a amount

   

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
