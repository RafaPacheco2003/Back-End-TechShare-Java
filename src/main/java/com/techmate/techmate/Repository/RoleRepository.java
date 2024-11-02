package com.techmate.techmate.Repository;
import java.util.*;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.UsuarioRole;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNombre(String nombre);


    Role findByName(String name);

   
}
