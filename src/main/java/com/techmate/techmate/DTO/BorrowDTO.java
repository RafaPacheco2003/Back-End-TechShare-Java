package com.techmate.techmate.DTO;

import java.util.Date;
import java.util.List;

import com.techmate.techmate.Entity.Status;
import lombok.Data;

@Data
public class BorrowDTO {
    
    private int borrowId;
    private Date date;
    private Status status;
    private double amount;

    private int usuarioId;
    private String usuarioName;

    private int adminId;
    private String adminName;

    // Añadir la lista de detalles del préstamo
    private List<DetailsBorrowDTO> details; // Cambiado aquí
}
