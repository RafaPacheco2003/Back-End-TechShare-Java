package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techmate.techmate.Entity.Movements;
import java.util.Optional;

public interface MovementsRepository extends JpaRepository<Movements, Integer>{
    
}
