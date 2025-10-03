package com.techmate.techmate.Service.movements.query;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.techmate.techmate.Service.movements.mapper.MovementMapper;
import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.entity.MoveType;
import com.techmate.techmate.repository.MovementsRepository;

@Component
public class MovementQueryService {

    private final MovementsRepository movementsRepository;
    private final MovementMapper movementMapper;

    public MovementQueryService(MovementsRepository movementsRepository, MovementMapper movementMapper) {
        this.movementsRepository = movementsRepository;
        this.movementMapper = movementMapper;
    }

    public List<MovementsDTO> getAll() {
        return movementsRepository.findAll().stream().map(m -> movementMapper.toDTO(m, null, null)).collect(Collectors.toList());
    }

    public List<MovementsDTO> getByMoveType(MoveType moveType) {
        return movementsRepository.findByMoveType(moveType).stream().map(m -> movementMapper.toDTO(m, null, null)).collect(Collectors.toList());
    }

    public List<MovementsDTO> getByDateRange(Date start, Date end) {
        return movementsRepository.findByDateBetween(start, end).stream().map(m -> movementMapper.toDTO(m, null, null)).collect(Collectors.toList());
    }
}
