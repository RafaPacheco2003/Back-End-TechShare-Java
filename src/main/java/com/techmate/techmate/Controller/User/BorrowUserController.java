// BorrowUserController.java
package com.techmate.techmate.Controller.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.DTO.DetailsBorrowDTO;

import jakarta.servlet.http.HttpServletRequest;

import com.techmate.techmate.Service.User.BorrowUserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "https://tech-share.vercel.app") // Permitir solicitudes desde tu frontend
@RequestMapping("/borrow")
public class BorrowUserController {

    @Autowired
    private BorrowUserService borrowUserService;

    @PostMapping("/create")
    public ResponseEntity<?> createBorrow(
            @RequestParam("details") String detailsJson,
            HttpServletRequest request // Pasamos los detalles como un String JSON
    ) {
        BorrowDTO borrowDTO = new BorrowDTO();
        borrowDTO.setDate(new Date());

        String token = request.getHeader("Authorization");
        Integer userId = null;
        List<Integer> roles = new ArrayList<>();

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                userId = borrowUserService.getUserIdFromToken(token);
                borrowDTO.setUsuarioId(userId);

                Optional<List<Integer>> rolesOptional = borrowUserService.getRolesFromToken(token);
                if (rolesOptional.isPresent()) {
                    roles = rolesOptional.get();
                }
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }

        List<DetailsBorrowDTO> details = convertJsonToDetailsList(detailsJson);
        borrowDTO.setDetails(details);

        try {
            BorrowDTO createdBorrow = borrowUserService.createBorrowDTO(borrowDTO, roles);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBorrow);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private List<DetailsBorrowDTO> convertJsonToDetailsList(String detailsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(detailsJson, new TypeReference<List<DetailsBorrowDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir el JSON a la lista de detalles.", e);
        }
    }

    @GetMapping("/user/borrows")
    public ResponseEntity<List<BorrowDTO>> getAllBorrows(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Integer userId = null;

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                userId = borrowUserService.getUserIdFromToken(token);
                List<BorrowDTO> borrows = borrowUserService.getAllBorrowsByUserId(userId);
                return ResponseEntity.ok(borrows);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
