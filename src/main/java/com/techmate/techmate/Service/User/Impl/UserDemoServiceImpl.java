package com.techmate.techmate.Service.User.Impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.UsuarioDTO;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Entity.UsuarioRole;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Repository.UsuarioRoleRepository;
import com.techmate.techmate.Service.TokenService;
import com.techmate.techmate.Service.User.UserDemoService;

import io.jsonwebtoken.Claims;

@Service
public class UserDemoServiceImpl implements UserDemoService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository userRepository; // Inyectamos el repositorio de usuario

    @Autowired
    private UsuarioRoleRepository usuarioRoleRepository;

    @Override
    public Optional<UsuarioDTO> getUserDetailsFromToken(String token) {
        // Obtener el userId del token
        Integer userId = tokenService.getUserIdFromToken(token);

        // Verificar si el ID es válido
        if (userId != null) {
            System.out.println("Buscando usuario con ID: " + userId); // Verificación del ID
            Optional<Usuario> usuarioOptional = userRepository.findById(userId);

            // Si el usuario existe, lo convertimos a DTO
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();

                // Buscar los roles del usuario mediante la tabla intermedia UsuarioRole
                List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByUsuarioId(userId);

                // Mapear los roles a un Set de nombres de roles
                Set<String> roles = usuarioRoles.stream()
                        .map(usuarioRole -> usuarioRole.getRole().getNombre()) // Asegurándonos de que 'Role' tiene el campo 'nombre'
                        .collect(Collectors.toSet());


                // Crear el DTO y mapear los datos necesarios
                UsuarioDTO usuarioDTO = new UsuarioDTO();
                usuarioDTO.setId(usuario.getId());
                usuarioDTO.setUserName(usuario.getUser_name());
                usuarioDTO.setFirstName(usuario.getFirst_name());
                usuarioDTO.setLastName(usuario.getLast_name());
                usuarioDTO.setEmail(usuario.getEmail());
                 usuarioDTO.setRoles(roles); // Establecer los roles en el DTO

                // Devolver el DTO envuelto en un Optional
                return Optional.of(usuarioDTO);
            }
        }

        // Si no se encuentra el usuario o el ID es inválido, devolver Optional vacío
        return Optional.empty();
    }

}
