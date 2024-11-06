package com.techmate.techmate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.Entity.Status;
import com.techmate.techmate.Exception.ResourceNotFoundException;
import com.techmate.techmate.Service.BorrowService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("admin/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // Actualizar el estado de un préstamo
    @PutMapping("/update/{borrowId}")
    public ResponseEntity<?> updateBorrowStatus(
            @PathVariable Integer borrowId,
            @RequestParam("status") Status newStatus,
            HttpServletRequest request) {

                BorrowDTO borrowDTO = new BorrowDTO();
                

                String token= request.getHeader("Authorization");
                Integer adminId = null;

                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
        
                    try {
        
                        adminId = borrowService.getUserIdFromToken(token);
                        System.out.println("Id de usuario extraido del token:  " + adminId);
                        
        
                    } catch (RuntimeException e) {
                        // TODO: handle exception
                        System.out.println("Error al extraer el ID del token: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                    }
                }


        try {
            borrowService.updateBorrowStatus(borrowId, newStatus, adminId);
            return ResponseEntity.ok("Borrow status updated successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Borrow record not found.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid status value.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the status.");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<BorrowDTO>> getAllBorrow() {
        try {

            List<BorrowDTO> borrowsList = borrowService.getAllBorrowDTO();

            if (borrowsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.ok(borrowsList);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BorrowDTO>> getBorrowByStatus(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy- MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        try {

            List<BorrowDTO> borrowList= borrowService.getBorrowByDate(startDate, endDate);

            if (borrowList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
