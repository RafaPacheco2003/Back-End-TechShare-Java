package com.techmate.techmate.Repository;

import com.techmate.techmate.Entity.DetailsBorrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailsBorrowRepository extends JpaRepository<DetailsBorrow, Integer> {
    // Métodos adicionales si es necesario
}
