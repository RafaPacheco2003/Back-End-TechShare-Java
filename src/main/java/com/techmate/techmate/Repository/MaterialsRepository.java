package com.techmate.techmate.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.Entity.Materials;

@Repository
public interface MaterialsRepository extends JpaRepository<Materials, Integer> {
    // Método para encontrar materiales por nombre del rol
    List<Materials> findByRoleNombre(String roleName);

    List<Materials> findByRoleNombreIn(List<String> roleNames);

}

