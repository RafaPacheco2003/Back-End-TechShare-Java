package com.techmate.techmate.Service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.UsuarioDTO;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Entity.UsuarioRole;
import com.techmate.techmate.Exception.DTO.UserNotFoundException;
import com.techmate.techmate.Repository.RoleRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Repository.UsuarioRoleRepository;
import com.techmate.techmate.Service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsuarioRepository userRepository;

    @Autowired
    private RoleRepository roleRepository; // Asegúrate de que este es el repositorio correcto

    @Autowired
    private UsuarioRoleRepository usuarioRoleRepository;

    private UsuarioDTO convertToDTO(Usuario usuario, Set<String> roles) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setUserName(usuario.getUser_name());
        usuarioDTO.setFirstName(usuario.getFirst_name());
        usuarioDTO.setLastName(usuario.getLast_name());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setRoles(roles);
        return usuarioDTO;
    }

    @Override
    public List<UsuarioDTO> getAllUser() {
        List<Usuario> usuarios = userRepository.findAll();

        if (usuarios.isEmpty()) {
            throw new UserNotFoundException("No está disponible ningún usuario");
        }

        List<Integer> usuarioIds = usuarios.stream()
                .map(Usuario::getId)
                .collect(Collectors.toList());

        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByUsuarioIds(usuarioIds);

        return usuarios.stream()
                .map(usuario -> {
                    Set<String> roles = usuarioRoles.stream()
                            .filter(usuarioRole -> usuarioRole.getUsuario().getId().equals(usuario.getId()))
                            .map(usuarioRole -> usuarioRole.getRole().getNombre())
                            .collect(Collectors.toSet());
                    return convertToDTO(usuario, roles);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioDTO> findUserById(Integer id) {
        return userRepository.findById(id)
                .map(usuario -> {
                    Set<String> roles = usuarioRoleRepository.findByUsuarioIds(List.of(id)).stream()
                            .map(usuarioRole -> usuarioRole.getRole().getNombre())
                            .collect(Collectors.toSet());
                    return convertToDTO(usuario, roles);
                });
    }

    @Override
    public void deleteUsuser(Integer id) {
        Optional<Usuario> usuarioOpt = userRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new UserNotFoundException("El usuario con ID " + id + " no fue encontrado.");
        }

        userRepository.deleteById(id);
    }

    @Override
    public Optional<UsuarioDTO> updateUser(Integer id, UsuarioDTO usuarioDTO) {
        // Buscar el usuario existente
        Optional<Usuario> usuarioOpt = userRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            // Lanza una excepción si no se encuentra el usuario
            throw new UserNotFoundException("El usuario con ID " + id + " no fue encontrado.");
        }

        // Obtener el usuario actual
        Usuario usuario = usuarioOpt.get();

        // Actualizar los campos del usuario con los nuevos valores de usuarioDTO solo
        // si no son null
        if (usuarioDTO.getUserName() != null) {
            usuario.setUser_name(usuarioDTO.getUserName());
        }
        if (usuarioDTO.getFirstName() != null) {
            usuario.setFirst_name(usuarioDTO.getFirstName());
        }
        if (usuarioDTO.getLastName() != null) {
            usuario.setLast_name(usuarioDTO.getLastName());
        }
        if (usuarioDTO.getEmail() != null) {
            usuario.setEmail(usuarioDTO.getEmail());
        }

        // Actualizar roles solo si se proporcionaron nuevos roles
        if (usuarioDTO.getRoles() != null && !usuarioDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : usuarioDTO.getRoles()) {
                Optional<Role> roleOpt = roleRepository.findByNombre(roleName);
                if (roleOpt.isEmpty()) {
                    throw new UserNotFoundException("El rol '" + roleName + "' no existe.");
                }
                roles.add(roleOpt.get());
            }
            usuario.setRoles(roles); // Asignar los nuevos roles solo si se proporcionaron
        }

        // Guardar el usuario actualizado
        Usuario updatedUsuario = userRepository.save(usuario);

        // Convertir el usuario actualizado a DTO
        Set<String> updatedRoles = updatedUsuario.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toSet());

        // Retornar el UsuarioDTO actualizado
        return Optional.of(convertToDTO(updatedUsuario, updatedRoles));
    }

}