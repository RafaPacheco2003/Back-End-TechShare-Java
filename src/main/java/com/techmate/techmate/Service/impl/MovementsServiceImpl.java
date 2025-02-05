package com.techmate.techmate.Service.impl;


import java.util.stream.Collectors;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;


import com.techmate.techmate.DTO.MovementsDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.MoveType;
import com.techmate.techmate.Entity.Movements;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.MovementsRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Security.TokenUtils;
import com.techmate.techmate.Security.UserDetailsServiceImpl;
import com.techmate.techmate.Service.MaterialsService;
import com.techmate.techmate.Service.MovementsService;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class MovementsServiceImpl implements MovementsService {

    @Autowired
    private MovementsRepository movementsRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UserDetailsServiceImpl userService;

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private MaterialsService materialsService;

    private Movements convertToEntity(MovementsDTO movementsDTO, Integer userId) {
        Movements movements = new Movements();

        movements.setMovementsId(movementsDTO.getMovementsId());
        // Asignar MoveType directamente desde el DTO
        movements.setMoveType(movementsDTO.getMoveType());
        String comment = movementsDTO.getComment();

        movements.setQuantity(movementsDTO.getQuantity());
        movements.setDate(movementsDTO.getDate());
        movements.setComment(movementsDTO.getComment());

        // Aquí asignamos el ID de usuario obtenido del token
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));
        movements.setUsuario(usuario);

        Materials materials = materialsRepository.findById(movementsDTO.getMaterialsId())
                .orElseThrow(
                        () -> new RuntimeException("Material no encontrado con ID: " + movementsDTO.getMaterialsId()));
        movements.setMaterials(materials);

        return movements;
    }

    private MovementsDTO convertToDTO(Movements movements) {
        MovementsDTO dto = new MovementsDTO();
        dto.setMovementsId(movements.getMovementsId());

        // Asignar MoveType directamente, ya que es del mismo tipo
        dto.setMoveType(movements.getMoveType());

        String comment = movements.getComment();
        System.out.println(comment + "A la hora de convertir a dto");
        dto.setQuantity(movements.getQuantity());
        dto.setDate(movements.getDate());
        dto.setComment(comment);

        // Obtener y asignar IDs y nombres
        dto.setAdminId(movements.getUsuario().getId());
        dto.setMaterialsId(movements.getMaterials().getMaterialsId());

        // Obtener y asignar nombres de Usuario y Materials mediante los servicios
        dto.setAdminName(userService.getUsuarioUsernamById(movements.getUsuario().getId()));
        dto.setMaterialsName(materialsService.getMaterialsNameById(movements.getMaterials().getMaterialsId()));

        return dto;
    }

    @Override
    public MovementsDTO createMovementsDTO(MovementsDTO movementsDTO, Integer userId) {

        // Verificar si la cantidad es menor o igual a 0
        if (movementsDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        String comment = movementsDTO.getComment() + "sevice create";
        movementsDTO.setComment(comment);
        // Asignar la fecha actual al movimientFo
        movementsDTO.setDate(new Date());

        // Convertir DTO a entidad
        Movements movements = convertToEntity(movementsDTO, userId);
        // Obtener el material correspondiente antes de ajustar el stock
        Materials materials = materialsRepository.findById(movementsDTO.getMaterialsId())
                .orElseThrow(
                        () -> new RuntimeException("Material no encontrado con ID: " + movementsDTO.getMaterialsId()));

        adjustMaterialStock(materials, movements);
        materialsRepository.save(materials);

        movements = movementsRepository.save(movements);
        return convertToDTO(movements);
    }

    // Ajustar stock de material
    private void adjustMaterialStock(Materials materials, Movements movements) {

        switch (movements.getMoveType()) {
            case IN:
                // Incrementar el stock total y el borrowable_stock cuando entra material
                materials.setBorrowable_stock(materials.getBorrowable_stock() + movements.getQuantity());
                materials.setStock(materials.getStock() + movements.getQuantity());
                break;
            case OUT:
                // Verificar si el stock total es suficiente
                if (materials.getStock() < movements.getQuantity()) {
                    throw new IllegalArgumentException(
                            "Stock insuficiente para el material con ID: " + materials.getMaterialsId());
                }
                // Reducir el borrowable_stock y el stock total cuando sale material
                materials.setBorrowable_stock(materials.getBorrowable_stock() - movements.getQuantity());
                materials.setStock(materials.getStock() - movements.getQuantity());
                break;
            case ADJUST:
                // En el caso de ajustes, se ajusta tanto el borrowable_stock como el stock
                // total
                int difference = movements.getQuantity() - materials.getStock();
                materials.setBorrowable_stock(materials.getBorrowable_stock() + difference);
                materials.setStock(movements.getQuantity());
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento inválido");
        }
    }

    @Override
    public MovementsDTO getMovementsByID(Integer movementsId) {
        Movements movements = movementsRepository.findById(movementsId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + movementsId));
        return convertToDTO(movements);
    }

    @Override
    public List<MovementsDTO> getAllMovementsDTO() {
        // Recuperar todos los movimientos y convertirlos a DTO
        return movementsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovementsDTO> getMovementsByType(String type) {
        MoveType moveType;

        // Convertir la cadena de texto a enum MoveType usando un switch-case
        switch (type.toUpperCase()) {
            case "IN":
                moveType = MoveType.IN;
                break;
            case "OUT":
                moveType = MoveType.OUT;
                break;
            case "ADDJUST":
                moveType = MoveType.ADJUST;
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento inválido: " + type);
        }

        // Filtrar y convertir los movimientos a DTOs en una sola operación
        return movementsRepository.findByMoveType(moveType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MovementsDTO> getMovementsByDate(Date startDate, Date endDate) {
        // Filtrar movimientos en el rango de fechas y convertir a DTOs
        return movementsRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMovementById(Integer movementsId) {
        if (movementsRepository.existsById(movementsId)) {
            
            movementsRepository.deleteById(movementsId);
        } else {
            throw new EntityNotFoundException("Movement not found with id: " + movementsId);
        }
    }



    public void decodeToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.replace("Bearer ", "");
            Claims claims = TokenUtils.decodeToken(token);

            if (claims != null) {
                String email = claims.getSubject(); // Obtener el email del token
                Integer userId = (Integer) claims.get("id"); // Obtener el ID del usuario
                

                // Aquí puedes utilizar la información decodificada
                System.out.println("Email: " + email);
                System.out.println("User ID: " + userId);
                
            } else {
                throw new RuntimeException("Token no válido");
            }
        } else {
            throw new RuntimeException("No se proporcionó un token");
        }
    }
    
    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        return TokenUtils.getRolesFromToken(token);
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

}
