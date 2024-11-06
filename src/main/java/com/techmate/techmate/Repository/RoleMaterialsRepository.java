package com.techmate.techmate.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.RoleMaterials;

@Repository
public interface RoleMaterialsRepository extends JpaRepository<RoleMaterials, Integer>{
    List<RoleMaterials> findByRole(Role role);

     // Definir el método personalizado para buscar por material
     List<RoleMaterials> findByMaterials(Materials material);
}
