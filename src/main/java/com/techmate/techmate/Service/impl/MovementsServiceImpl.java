package com.techmate.techmate.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.techmate.techmate.DTO.MovementsDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.MoveType;
import com.techmate.techmate.Entity.Movements;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.MovementsRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Security.UserDetailsServiceImpl;
import com.techmate.techmate.Service.MaterialsService;
import com.techmate.techmate.Service.MovementsService;

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

    private Movements convertToEntity(MovementsDTO movementsDTO) {
        Movements movements = new Movements();
    
        movements.setMovements_id(movementsDTO.getMovementsId());
        // Asignar MoveType directamente desde el DTO
        movements.setMoveType(movementsDTO.getMoveType());
    
        movements.setComment(movementsDTO.getComment());
        movements.setQuantity(movementsDTO.getQuantity());
        movements.setDate(movementsDTO.getDate());
    
        Usuario usuario = usuarioRepository.findById(movementsDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + movementsDTO.getUsuarioId()));
        movements.setUsuario(usuario);
    
        Materials materials = materialsRepository.findById(movementsDTO.getMaterialsId())
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + movementsDTO.getMaterialsId()));
        movements.setMaterials(materials);
    
        return movements;
    }
    

    private MovementsDTO convertToDTO(Movements movements) {
        MovementsDTO dto = new MovementsDTO();
    
        // Asignar MoveType directamente, ya que es del mismo tipo
        dto.setMoveType(movements.getMoveType());
    
        dto.setComment(movements.getComment());
        dto.setQuantity(movements.getQuantity());
        dto.setDate(movements.getDate());
    
        // Obtener y asignar IDs y nombres
        dto.setUsuarioId(movements.getUsuario().getId());
        dto.setMaterialsId(movements.getMaterials().getMaterialsId());
    
        // Obtener y asignar nombres de Usuario y Materials mediante los servicios
        dto.setUsuarioName(userService.getUsuarioUsernamById(movements.getUsuario().getId()));
        dto.setMaterialsName(materialsService.getMaterialsNameById(movements.getMaterials().getMaterialsId()));
    
        return dto;
    }
    

    @Override
public MovementsDTO createMovementsDTO(MovementsDTO movementsDTO) {
    // Convertir DTO a entidad
    Movements movements = convertToEntity(movementsDTO);

    // Obtener el material correspondiente antes de ajustar el stock
    Materials materials = materialsRepository.findById(movementsDTO.getMaterialsId())
            .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + movementsDTO.getMaterialsId()));

    // Ajustar el stock según el tipo de movimiento
    if (movements.getMoveType() == MoveType.IN) {
        int plusStock = materials.getStock() + movements.getQuantity();
        materials.setStock(plusStock);
    } else if (movements.getMoveType() == MoveType.OUT) {
        int minusStock = materials.getStock() - movements.getQuantity();
        materials.setStock(minusStock);
    } else if (movements.getMoveType() == MoveType.ADDJUST) {
        materials.setStock(movements.getQuantity());
    }

    materialsRepository.save(materials);
    
    // Guardar en el repositorio
    movements = movementsRepository.save(movements);
    // Convertir de nuevo a DTO para devolverlo
    return convertToDTO(movements);
}


    @Override
    public MovementsDTO getMovementsByID(Integer movementsId) {
        Movements movements = movementsRepository.findById(movementsId)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado con ID: " + movementsId));
        return convertToDTO(movements);
    }
}
