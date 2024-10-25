package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.techmate.techmate.Entity.MoveType;
import com.techmate.techmate.Entity.Movements;

import java.util.Date;
import java.util.List;

public interface MovementsRepository extends JpaRepository<Movements, Integer>{
    
    // Método para buscar movimientos por tipo
    List<Movements> findByMoveType(MoveType moveType);

    List<Movements> findByDateBetween(Date startDate, Date endDate);
}
