package com.techmate.techmate.Service.impl;


import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import java.util.*;

import com.techmate.techmate.Service.MaterialsService;
import com.techmate.techmate.Service.MovementsService;
import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.MoveType;
import com.techmate.techmate.entity.Movements;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.MovementsRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.security.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class MovementsServiceImpl implements MovementsService {

    private final MovementsRepository movementsRepository;
    private final UsuarioRepository usuarioRepository;
    private final UserDetailsServiceImpl userService;
    private final MaterialsRepository materialsRepository;
    private final MaterialsService materialsService;
    private final com.techmate.techmate.Service.movements.mapper.MovementMapper movementMapper;
    private final com.techmate.techmate.Service.movements.validator.MovementValidator movementValidator;
    private final com.techmate.techmate.Service.movements.manager.MovementStockManager movementStockManager;
    private final com.techmate.techmate.Service.movements.query.MovementQueryService movementQueryService;

    public MovementsServiceImpl(MovementsRepository movementsRepository,
                                UsuarioRepository usuarioRepository,
                                UserDetailsServiceImpl userService,
                                MaterialsRepository materialsRepository,
                                MaterialsService materialsService,
                                com.techmate.techmate.Service.movements.mapper.MovementMapper movementMapper,
                                com.techmate.techmate.Service.movements.validator.MovementValidator movementValidator,
                                com.techmate.techmate.Service.movements.manager.MovementStockManager movementStockManager,
                                com.techmate.techmate.Service.movements.query.MovementQueryService movementQueryService) {
        this.movementsRepository = movementsRepository;
        this.usuarioRepository = usuarioRepository;
        this.userService = userService;
        this.materialsRepository = materialsRepository;
        this.materialsService = materialsService;
        this.movementMapper = movementMapper;
        this.movementValidator = movementValidator;
        this.movementStockManager = movementStockManager;
        this.movementQueryService = movementQueryService;
    }

    private Movements convertToEntity(MovementsDTO movementsDTO, Integer userId) {
        Usuario usuario = usuarioRepository.findById(userId).orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException("Usuario no encontrado"));
        Materials materials = materialsRepository.findById(movementsDTO.getMaterialsId()).orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException("Material no encontrado"));
        return movementMapper.toEntity(movementsDTO, usuario, materials);
    }

    private MovementsDTO convertToDTO(Movements movements) {
        String adminName = userService.getUsuarioUsernamById(movements.getUsuario().getId());
        String materialName = materialsService.getMaterialsNameById(movements.getMaterials().getMaterialsId());
        return movementMapper.toDTO(movements, adminName, materialName);
    }

    @Override
    public MovementsDTO createMovementsDTO(MovementsDTO movementsDTO, Integer userId) {

        movementValidator.validateQuantity(movementsDTO);

        String comment = movementsDTO.getComment();
        movementsDTO.setComment(comment);
        // Asignar la fecha actual al movimientFo
        movementsDTO.setDate(new Date());

    Movements movements = convertToEntity(movementsDTO, userId);
    Materials materials = movements.getMaterials();
    movementStockManager.adjustMaterialStock(materials, movements);
    materialsRepository.save(materials);
    movements = movementsRepository.save(movements);
    return convertToDTO(movements);
    }

    // Ajustar stock de material
    // Stock adjustments now delegated to MovementStockManager

    @Override
    public MovementsDTO getMovementsByID(Integer movementsId) {
    Movements movements = movementsRepository.findById(movementsId)
        .orElseThrow(() -> new com.techmate.techmate.exception.NotFoundException("Movimiento no encontrado"));
        return convertToDTO(movements);
    }

    @Override
    public List<MovementsDTO> getAllMovementsDTO() {
        return movementQueryService.getAll();
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
            case "ADJUST":
                moveType = MoveType.ADJUST;
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento inválido: " + type);
        }

    return movementQueryService.getByMoveType(moveType);
    }

    @Override
    public List<MovementsDTO> getMovementsByDate(Date startDate, Date endDate) {
        return movementQueryService.getByDateRange(startDate, endDate);
    }

    @Override
    public void deleteMovementById(Integer movementsId) {
        if (movementsRepository.existsById(movementsId)) {
            
            movementsRepository.deleteById(movementsId);
        } else {
            throw new EntityNotFoundException("Movement not found");
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
            throw new com.techmate.techmate.exception.BusinessException("INVALID_TOKEN", "Token no válido");
            }
        } else {
            throw new com.techmate.techmate.exception.BusinessException("MISSING_TOKEN", "No se proporcionó un token");
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
