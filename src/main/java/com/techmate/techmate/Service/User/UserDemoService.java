package com.techmate.techmate.Service.User;

import java.util.Optional;

import com.techmate.techmate.dto.UsuarioDTO;
import com.techmate.techmate.entity.Usuario;

public interface UserDemoService {
Optional<UsuarioDTO> getUserDetailsFromToken(String token);
}
