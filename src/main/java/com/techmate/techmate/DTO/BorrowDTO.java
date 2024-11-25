package com.techmate.techmate.DTO;

import java.util.Date;
import java.util.List;

import com.techmate.techmate.Entity.Status;

import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
public class BorrowDTO {
    
    private int borrowId;
    private Date date;
   
    private Date startDate;

   
    private Date endDate;

    private Date returnDate;
    private Status status;
    private double amount;

    private int usuarioId;
    private String usuarioName;

    private int adminId;
    private String adminName;

    // Añadir la lista de detalles del préstamo
    private List<DetailsBorrowDTO> details; // Cambiado aquí
}
