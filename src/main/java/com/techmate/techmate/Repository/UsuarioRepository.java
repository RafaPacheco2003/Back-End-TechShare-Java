package com.techmate.techmate.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import com.techmate.techmate.Entity.Movements;
import com.techmate.techmate.Entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{
    
    Optional<Usuario> findById(Integer id);  // Método para buscar el usuario por ID
    
    Optional <Usuario> getUsuarioUsernamById(int usernameId);
    Optional<Usuario> findOneByEmail(String email);

    Optional<Usuario> findByEmail(String email);

}
