package com.techmate.techmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techmate.techmate.entity.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    
    Optional<Usuario> findById(Integer id);  // Método para buscar el usuario por ID
    
    Optional <Usuario> getUsuarioUsernamById(int usernameId);
    Optional<Usuario> findOneByEmail(String email);

    Optional<Usuario> findByEmail(String email);

}
