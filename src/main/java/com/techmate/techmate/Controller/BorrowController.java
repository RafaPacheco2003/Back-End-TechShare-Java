package com.techmate.techmate.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.DTO.DetailsBorrowDTO;
import com.techmate.techmate.Service.BorrowService;
import com.techmate.techmate.Entity.Status;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // Crear un nuevo préstamo
    @PostMapping("/create")
    public BorrowDTO createBorrow(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @RequestParam("status") Status status,
            @RequestParam("details") String detailsJson // Pasamos los detalles como un String JSON
    ) throws Exception {
        BorrowDTO borrowDTO = new BorrowDTO();
        borrowDTO.setDate(date);
        borrowDTO.setStatus(status);

        // Convertir el JSON de detalles en una lista de objetos DetailsBorrowDTO
        List<DetailsBorrowDTO> details = convertJsonToDetailsList(detailsJson);
        borrowDTO.setDetails(details);

        return borrowService.createBorrowDTO(borrowDTO);
    }

    // Método para convertir el JSON en una lista de detalles
    private List<DetailsBorrowDTO> convertJsonToDetailsList(String detailsJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Conversion de JSON a lista de DetailsBorrowDTO
            return objectMapper.readValue(detailsJson, new TypeReference<List<DetailsBorrowDTO>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir el JSON a la lista de detalles.", e);
        }
    }
}
