package com.techmate.techmate.Controller;

import java.util.*;

// ...existing imports...
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.techmate.techmate.Service.MovementsService;
import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.dto.MovementResponse;
import com.techmate.techmate.Service.movements.mapper.MovementsMapper;
import com.techmate.techmate.entity.MoveType;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
@RestController
@RequestMapping("/admin/movement")
public class MovementsController {

    private final MovementsService movementsService;
    private final MovementsMapper movementsMapper;

    // Crear un nuevo movimiento
    public MovementsController(MovementsService movementsService, MovementsMapper movementsMapper) {
        this.movementsService = movementsService;
        this.movementsMapper = movementsMapper;
    }

    @PostMapping("/create")
public ResponseEntity<?> createMovement(
        @RequestParam("quantity") Integer quantity,
        @RequestParam("moveType") MoveType moveType,
        @RequestParam("id_material") Integer idMaterial,
        @RequestParam(value = "comment", required = false) String comment, // Agregar comentario opcional
        HttpServletRequest request) {

    MovementsDTO movementsDTO = new MovementsDTO();
    movementsDTO.setQuantity(quantity);
    movementsDTO.setMoveType(moveType);
    movementsDTO.setMaterialsId(idMaterial);
    movementsDTO.setDate(new Date());
    movementsDTO.setComment(comment); // Establecer el comentario

    String token = request.getHeader("Authorization");
    Integer userId = null;

    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7);

        try {
            // Extraer el ID de usuario del token
            userId = movementsService.getUserIdFromToken(token);
            System.out.println("ID de usuario extraído del token: " + userId);

            // Extraer y mostrar roles desde el token
            Optional<List<Integer>> rolesOptional = movementsService.getRolesFromToken(token);
            if (rolesOptional.isPresent()) {
                List<Integer> roles = rolesOptional.get();
                System.out.println("Roles extraídos del token: " + roles);
            } else {
                System.out.println("No se encontraron roles en el token.");
            }

        } catch (RuntimeException e) {
            System.out.println("Error al extraer el ID del token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al extraer el ID del token: " + e.getMessage());
        }
    } else {
        System.out.println("Token no proporcionado o formato incorrecto");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no proporcionado o formato incorrecto");
    }

    try {
        MovementsDTO createdMovement = movementsService.createMovementsDTO(movementsDTO, userId);
        MovementResponse resp = movementsMapper.toResponse(createdMovement);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    } catch (Exception e) {
        e.printStackTrace(); // Mostrar más detalles del error en consola
        // Aquí puedes personalizar el mensaje de error que deseas devolver
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el movimiento: " + e.getMessage());
    }
}


    // Obtener un movimiento por ID
    @GetMapping("/{id}")
    public ResponseEntity<MovementResponse> getMovementById(@RequestParam("id") Integer id) {
        try {
            MovementsDTO movementsDTO = movementsService.getMovementsByID(id);
            if (movementsDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            MovementResponse resp = movementsMapper.toResponse(movementsDTO);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovementResponse>> getAllMovements() {
        try {
            // Llamar al servicio para obtener la lista de movimientos
            List<MovementsDTO> movementsList = movementsService.getAllMovementsDTO();

            // Verificar si la lista está vacía
            if (movementsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            // Mapear a response público y devolver la lista
            List<MovementResponse> resp = movementsList.stream()
                    .map(movementsMapper::toResponse)
                    .toList();

            // Devolver la lista de movimientos con el código HTTP 200 OK
            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            // En caso de error, devolver un estado de error interno del servidor
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<MovementResponse>> getMovementsByType(@PathVariable String type) {
        try {
            List<MovementsDTO> movementDTOsList = movementsService.getMovementsByType(type);

            if (movementDTOsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            List<MovementResponse> resp = movementDTOsList.stream()
                    .map(movementsMapper::toResponse)
                    .toList();

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/filterByDate")
    public ResponseEntity<List<MovementResponse>> getMovementsByDate(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        try {
            List<MovementsDTO> movementsDTOs = movementsService.getMovementsByDate(startDate, endDate);

            if (movementsDTOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            List<MovementResponse> resp = movementsDTOs.stream()
                    .map(movementsMapper::toResponse)
                    .toList();

            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/decode-token")
    public void decodeToken(HttpServletRequest request) {
        movementsService.decodeToken(request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMovement(@PathVariable Integer id) {
        try {
            movementsService.deleteMovementById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500 Internal Server Error
        }
    }

}
