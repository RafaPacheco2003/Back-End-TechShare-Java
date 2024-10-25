package com.techmate.techmate.Service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.MovementsDTO;

import jakarta.servlet.http.HttpServletRequest;

@Service
public interface MovementsService {

    MovementsDTO createMovementsDTO(MovementsDTO movementsDTO, Integer userId);

    MovementsDTO getMovementsByID(Integer movementsId);

    List<MovementsDTO> getAllMovementsDTO();

    List<MovementsDTO> getMovementsByType(String type);

    List<MovementsDTO> getMovementsByDate(Date startDate, Date endDate);

    void deleteMovementById(Integer movementsId); // Método para eliminar

    void decodeToken(HttpServletRequest request); // Método para obtener datos usando el token

    Integer getUserIdFromToken(String token);

}
