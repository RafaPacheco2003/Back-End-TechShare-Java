package com.techmate.techmate.Controller.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.DTO.DetailsBorrowDTO;

import jakarta.servlet.http.HttpServletRequest;

import com.techmate.techmate.Entity.Status;
import com.techmate.techmate.Service.User.BorrowUserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
@RequestMapping("/borrow")
public class BorrowUserController {

    @Autowired
    private BorrowUserService borrowUserService;

    @PostMapping("/create")
public ResponseEntity<?> createBorrow(
        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
        @RequestParam("details") String detailsJson,
        HttpServletRequest request // Pasamos los detalles como un String JSON
) {
    BorrowDTO borrowDTO = new BorrowDTO();
    borrowDTO.setDate(date);

    String token = request.getHeader("Authorization");
    Integer userId = null;
    List<Integer> roles = new ArrayList<>(); // Inicializa la lista de roles aquí

    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);

        try {
            userId = borrowUserService.getUserIdFromToken(token);
            System.out.println("Id de usuario extraído del token: " + userId);
            borrowDTO.setUsuarioId(userId);

            Optional<List<Integer>> rolesOptional = borrowUserService.getRolesFromToken(token);
            if (rolesOptional.isPresent()) {
                roles = rolesOptional.get(); // Asigna los roles aquí
                System.out.println("Roles extraídos del token: " + roles);
            } else {
                System.out.println("No se encontraron roles en el token.");
            }

        } catch (RuntimeException e) {
            System.out.println("Error al extraer el ID del token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Convertir el JSON de detalles en una lista de objetos DetailsBorrowDTO
    List<DetailsBorrowDTO> details = convertJsonToDetailsList(detailsJson);
    borrowDTO.setDetails(details);

    try {
        // Ahora los roles se pasan correctamente al servicio
        BorrowDTO createBorrowDTO = borrowUserService.createBorrowDTO(borrowDTO, roles);
        return ResponseEntity.status(HttpStatus.CREATED).body(createBorrowDTO);

    } catch (Exception e) {
        // Capturar la excepción y devolverla al frontend con el mensaje detallado
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}


    // Método para convertir el JSON en una lista de detalles
    private List<DetailsBorrowDTO> convertJsonToDetailsList(String detailsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Conversion de JSON a lista de DetailsBorrowDTO
            return objectMapper.readValue(detailsJson, new TypeReference<List<DetailsBorrowDTO>>() {
            });
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
                System.out.println("Error al extraer el ID del token: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
