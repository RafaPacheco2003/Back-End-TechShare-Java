package com.techmate.techmate.Controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.techmate.techmate.Service.MovementsService;
import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.dto.MovementResponse;
import com.techmate.techmate.Service.movements.mapper.MovementsMapper;
import com.techmate.techmate.entity.MoveType;
import com.techmate.techmate.security.TokenUtils;

import jakarta.persistence.EntityNotFoundException;

@CrossOrigin(origins = "http://localhost:3000") // Permitir solicitudes desde tu frontend
@RestController
@RequestMapping("/admin/movement")
public class MovementsController {

    private static final Logger log = LoggerFactory.getLogger(MovementsController.class);

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
        Authentication authentication) {

    MovementsDTO movementsDTO = new MovementsDTO();
    movementsDTO.setQuantity(quantity);
    movementsDTO.setMoveType(moveType);
    movementsDTO.setMaterialsId(idMaterial);
    movementsDTO.setDate(new Date());
    movementsDTO.setComment(comment); // Establecer el comentario

    Integer userId = null;

    try {
        // Obtener información del usuario desde el SecurityContext
        String userEmail = authentication.getName(); // Obtenemos el email del usuario autenticado
        log.info("Usuario autenticado: {}", userEmail);

        // Obtener roles del usuario
        String userRole = TokenUtils.getAuthenticatedUserRole();
        log.info("Rol del usuario: {}", userRole);

        // TODO: Implementar método en el servicio para obtener userId por email
        // Por ahora usamos un placeholder - en una implementación real:
        // userId = userService.getUserIdByEmail(userEmail);
        userId = extractUserIdFromAuthentication(authentication);

    } catch (Exception e) {
        log.error("Error al obtener información del usuario autenticado: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al obtener información del usuario: " + e.getMessage());
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
    public ResponseEntity<MovementResponse> getMovementById(@PathVariable("id") Integer id) {
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

    /**
     * Método helper para extraer userId del Authentication.
     */
    private Integer extractUserIdFromAuthentication(Authentication authentication) {
        try {
            if (authentication != null) {
                // Intentar extraer de diferentes formas según el tipo de Authentication
                if (authentication.getPrincipal() instanceof String) {
                    // El username/email está en principal
                    String userEmail = (String) authentication.getPrincipal();
                    // Aquí podrías buscar el usuario por email en la BD
                    // Por ahora usamos el token si está disponible
                    log.info("Usuario autenticado por email: {}", userEmail);
                }
                
                // Intentar obtener el ID desde los detalles del token JWT
                if (authentication.getDetails() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
                    if (details.containsKey("id")) {
                        return ((Number) details.get("id")).intValue();
                    }
                }
                
                // Último recurso: usar credentials si es un token
                if (authentication.getCredentials() != null) {
                    String token = authentication.getCredentials().toString();
                    return TokenUtils.getUserIdFromToken(token);
                }
            }
            log.warn("No se pudo extraer userId del Authentication, usando fallback");
            return 1; // Fallback temporal para desarrollo
        } catch (Exception e) {
            log.error("Error al extraer userId: {}", e.getMessage());
            return 1; // Fallback en caso de error
        }
    }

}
