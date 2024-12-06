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
@CrossOrigin(origins = "https://tech-share.vercel.app")
@RequestMapping("admin/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // Actualizar el estado de un préstamo
    @PutMapping("/update/{borrowId}")
public ResponseEntity<?> updateBorrowStatus(
        @PathVariable Integer borrowId,
        @RequestParam("status") Status newStatus,
        HttpServletRequest request) throws Exception {

    BorrowDTO borrowDTO = new BorrowDTO();

    borrowDTO.setStartDate(new Date());

    String token = request.getHeader("Authorization");
    Integer adminId = null;

    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);

        adminId = borrowService.getUserIdFromToken(token);
        System.out.println("Id de usuario extraído del token:  " + adminId);
    }

    borrowService.updateBorrowStatus(borrowId, newStatus, adminId);
    return ResponseEntity.ok("Estado del préstamo actualizado correctamente.");
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
    
    


}
