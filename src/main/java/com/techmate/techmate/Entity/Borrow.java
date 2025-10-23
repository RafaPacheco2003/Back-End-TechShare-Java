package com.techmate.techmate.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "loan")
@Data
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(name = "issue_date")
    private Date date;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    private Date startDate;  // Auto-mapea a start_date

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    private Date endDate;  // Auto-mapea a end_date

    @Temporal(TemporalType.DATE)
    @Column(name = "return_date")
    private Date returnDate;  // Auto-mapea a return_date

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "amount")
    private double amount;  // mapped to amount

   

    @ManyToOne
    @JoinColumn(name = "user_id")
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

    // Compatibility getters/setters for legacy code that used 'borrowId'
    public Integer getBorrowId() {
        return this.id;
    }

    public void setBorrowId(Integer borrowId) {
        this.id = borrowId;
    }
}
