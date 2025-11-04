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
    @Column(name = "id")
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(name = "borrow_date")
    private Date date;

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "return_date")
    private Date returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "resource_id")
    private Integer resourceId;

    private double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    // Legacy compatibility fields (not in DB)
    @Transient
    private Date startDate;
    
    @Transient
    private Usuario admin;
    
    @Transient
    private List<DetailsBorrow> details;

    // Compatibility getters/setters for legacy code that used 'borrowId'
    public Integer getBorrowId() {
        return this.id;
    }

    public void setBorrowId(Integer borrowId) {
        this.id = borrowId;
    }

    // Compatibility method for calculating total amount
    public double calculateTotalAmount() {
        if (details != null) {
            return details.stream()
                    .mapToDouble(DetailsBorrow::getTotalPrice)
                    .sum();
        }
        return 0;
    }
}
