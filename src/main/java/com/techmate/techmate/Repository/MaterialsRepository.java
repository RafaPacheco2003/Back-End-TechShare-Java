package com.techmate.techmate.Repository;

import java.util.List;

import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Movements;
import com.techmate.techmate.Entity.Role;

import java.util.*;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Integer> {
    //Sirve para buscar material por name
    Materials findByName(String name);

    List<Materials> findAllByOrderByPriceAsc(); // Para obtener materiales ordenados por precio ascendente
    List<Materials> findAllByOrderByPriceDesc(); // Para obtener materiales ordenados por precio descendente
    
   


    

}

