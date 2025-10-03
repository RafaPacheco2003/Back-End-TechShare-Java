package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techmate.techmate.entity.MoveType;
import com.techmate.techmate.entity.Movements;

import java.util.Date;
import java.util.List;

public interface MovementsRepository extends JpaRepository<Movements, Integer>{
    
    // Método para buscar movimientos por tipo
    List<Movements> findByMoveType(MoveType moveType);

    List<Movements> findByDateBetween(Date startDate, Date endDate);
}
