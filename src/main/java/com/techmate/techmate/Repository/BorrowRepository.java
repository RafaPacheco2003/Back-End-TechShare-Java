package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.Entity.Borrow;
@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Integer>{
    
}
