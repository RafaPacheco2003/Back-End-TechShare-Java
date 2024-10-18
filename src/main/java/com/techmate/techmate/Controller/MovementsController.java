package com.techmate.techmate.Controller;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import com.techmate.techmate.DTO.MovementsDTO;
import com.techmate.techmate.Entity.MoveType;
import com.techmate.techmate.Service.MovementsService;

@RestController
@RequestMapping("/admin/movement")
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    // Crear un nuevo movimiento
    @PostMapping("/create")
    public MovementsDTO createMovement(@RequestParam("quantity") Integer quantity,
                                        @RequestParam("moveType") MoveType moveType,
                                        @RequestParam("id_usuario") Integer idUsuario,
                                        @RequestParam("id_material") Integer idMaterial,
                                        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        MovementsDTO movementsDTO = new MovementsDTO();
        movementsDTO.setQuantity(quantity);
        movementsDTO.setMoveType(moveType);
        movementsDTO.setUsuarioId(idUsuario);
        movementsDTO.setMaterialsId(idMaterial);
        movementsDTO.setDate(date);
        return movementsService.createMovementsDTO(movementsDTO);
    }

    // Obtener un movimiento por ID
    @GetMapping("/get")
    public MovementsDTO getMovementById(@RequestParam("id") Integer id) {
        return movementsService.getMovementsByID(id);
    }
}
