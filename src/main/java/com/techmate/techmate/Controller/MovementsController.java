package com.techmate.techmate.Controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.techmate.techmate.DTO.MovementsDTO;
import com.techmate.techmate.Entity.MoveType;
import com.techmate.techmate.Service.MovementsService;
@CrossOrigin(origins = "http://localhost:5173") // Permitir solicitudes desde tu frontend
@RestController
@RequestMapping("/admin/movement")
public class MovementsController {

    @Autowired
    private MovementsService movementsService;

    // Crear un nuevo movimiento
    // Crear un nuevo movimiento
    @PostMapping("/create")
    public ResponseEntity<MovementsDTO> createMovement(
            @RequestParam("quantity") Integer quantity,
            @RequestParam("moveType") MoveType moveType,
            @RequestParam("id_usuario") Integer idUsuario,
            @RequestParam("id_material") Integer idMaterial) {

        MovementsDTO movementsDTO = new MovementsDTO();
        movementsDTO.setQuantity(quantity);
        movementsDTO.setMoveType(moveType);
        movementsDTO.setUsuarioId(idUsuario);
        movementsDTO.setMaterialsId(idMaterial);

        // Asignar la fecha actual automáticamente
        movementsDTO.setDate(new Date());

        try {
            MovementsDTO createdMovement = movementsService.createMovementsDTO(movementsDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Obtener un movimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MovementsDTO> getMovementById(@RequestParam("id") Integer id) {
        try {
            MovementsDTO movementsDTO = movementsService.getMovementsByID(id);
            if (movementsDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(movementsDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovementsDTO>> getAllMovements() {
        try {
            // Llamar al servicio para obtener la lista de movimientos
            List<MovementsDTO> movementsList = movementsService.getAllMovementsDTO();

            // Verificar si la lista está vacía
            if (movementsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            // Devolver la lista de movimientos con el código HTTP 200 OK
            return ResponseEntity.ok(movementsList);

        } catch (Exception e) {
            // En caso de error, devolver un estado de error interno del servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MovementsDTO>> getMovementsByType(@PathVariable String type) {
        try {
            List<MovementsDTO> movementDTOsList = movementsService.getMovementsByType(type);
    
            if (movementDTOsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
    
            return ResponseEntity.ok(movementDTOsList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    


}
