package com.techmate.techmate.Service.User;

import java.util.Optional;

import com.techmate.techmate.DTO.UsuarioDTO;
import com.techmate.techmate.Entity.Usuario;

public interface UserDemoService {
Optional<UsuarioDTO> getUserDetailsFromToken(String token);
}
