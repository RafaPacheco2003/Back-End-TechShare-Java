package com.techmate.techmate.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.MovementsDTO;

@Service
public interface MovementsService {
    
    MovementsDTO createMovementsDTO (MovementsDTO movementsDTO);
    
    MovementsDTO getMovementsByID(Integer movementsId);
    List<MovementsDTO> getAllMovementsDTO();
    List<MovementsDTO> getMovementsByType(String type);


}
