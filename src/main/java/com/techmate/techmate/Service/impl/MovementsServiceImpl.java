package com.techmate.techmate.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.stereotype.Service;
import java.util.*;

import com.techmate.techmate.service.MaterialsService;
import com.techmate.techmate.service.MovementsService;
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

    private static final Logger log = LoggerFactory.getLogger(MovementsServiceImpl.class);

    private final MovementsRepository movementsRepository;
    private final UsuarioRepository usuarioRepository;
    private final UserDetailsServiceImpl userService;
    private final MaterialsRepository materialsRepository;
    private final MaterialsService materialsService;
    private final com.techmate.techmate.service.movements.mapper.MovementMapper movementMapper;
    private final com.techmate.techmate.service.movements.validator.MovementValidator movementValidator;
    private final com.techmate.techmate.service.movements.manager.MovementStockManager movementStockManager;
    private final com.techmate.techmate.service.movements.query.MovementQueryService movementQueryService;

    public MovementsServiceImpl(MovementsRepository movementsRepository,
                                UsuarioRepository usuarioRepository,
                                UserDetailsServiceImpl userService,
                                MaterialsRepository materialsRepository,
                                MaterialsService materialsService,
                                com.techmate.techmate.service.movements.mapper.MovementMapper movementMapper,
                                com.techmate.techmate.service.movements.validator.MovementValidator movementValidator,
                                com.techmate.techmate.service.movements.manager.MovementStockManager movementStockManager,
                                com.techmate.techmate.service.movements.query.MovementQueryService movementQueryService) {
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
            case "STOCK_ADD":
            case "IN":
                moveType = MoveType.STOCK_ADD;
                break;
            case "RETURN":
            case "OUT":
                moveType = MoveType.RETURN;
                break;
            case "BORROW":
                moveType = MoveType.BORROW;
                break;
            case "ADJUSTMENT":
            case "ADJUST":
                moveType = MoveType.ADJUSTMENT;
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
                    Object idClaim = claims.get("id");
                    Integer userId = null;
                
                // Validar y convertir ID de forma segura
                if (idClaim != null) {
                    if (idClaim instanceof Integer) {
                        userId = (Integer) idClaim;
                    } else if (idClaim instanceof Long) {
                        userId = ((Long) idClaim).intValue();
                    } else if (idClaim instanceof Number) {
                        userId = ((Number) idClaim).intValue();
                    } else {
                        try {
                            userId = Integer.parseInt(idClaim.toString());
                        } catch (NumberFormatException e) {
                            throw new com.techmate.techmate.exception.BusinessException("INVALID_TOKEN", "ID de usuario inválido en token");
                        }
                    }
                }
                
                    // Log para evitar avisos de variables no usadas y para trazabilidad
                    if (email != null) log.debug("Token email: {}", email);
                    if (userId != null) log.debug("Token userId: {}", userId);
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

