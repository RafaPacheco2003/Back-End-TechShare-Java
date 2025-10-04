package com.techmate.techmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.entity.Materials;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Integer> {
    //Sirve para buscar material por name
    Materials findByName(String name);

    List<Materials> findAllByOrderByPriceAsc(); // Para obtener materiales ordenados por precio ascendente
    List<Materials> findAllByOrderByPriceDesc(); // Para obtener materiales ordenados por precio descendente
    
   


    

}

