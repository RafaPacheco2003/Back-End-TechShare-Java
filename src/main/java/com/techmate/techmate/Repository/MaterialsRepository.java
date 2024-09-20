package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.Entity.Materials;

@Repository
public interface MaterialsRepository  extends JpaRepository<Materials, Integer>{
    
}
