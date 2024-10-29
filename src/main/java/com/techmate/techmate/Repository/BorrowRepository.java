package com.techmate.techmate.Repository;

import java.util.List;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techmate.techmate.Entity.Borrow;
import com.techmate.techmate.Entity.Status; // Cambia esto para importar tu clase Status

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer> {
    
    List<Borrow> findByUsuarioId(Integer usuarioId);
    List<Borrow> findByStatus(Status status); // Asegúrate de que sea tu Status

    List<Borrow> findByDateBetween(Date startDate, Date endDate); // Corregido aquí
}
