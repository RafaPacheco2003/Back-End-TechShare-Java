package com.techmate.techmate.Service;

import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.MovementsDTO;

@Service
public interface MovementsService {
    
    MovementsDTO createMovementsDTO (MovementsDTO movementsDTO);
    
    MovementsDTO getMovementsByID(Integer movementsId);


}
