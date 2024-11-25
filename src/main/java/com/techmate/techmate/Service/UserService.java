package com.techmate.techmate.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.techmate.techmate.DTO.UsuarioDTO;

public interface UserService {
    List<UsuarioDTO> getAllUser();
    
    Optional<UsuarioDTO> findUserById(Integer id);
    
    void deleteUsuser(Integer id);
    
    // Agregado método de actualización
    Optional<UsuarioDTO> updateUser(Integer id, UsuarioDTO usuarioDTO);
}
