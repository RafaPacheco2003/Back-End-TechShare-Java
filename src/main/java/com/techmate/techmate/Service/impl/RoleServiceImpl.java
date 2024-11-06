package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.RoleDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.UsuarioRole;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.RoleRepository;
import com.techmate.techmate.Repository.UsuarioRoleRepository;
import com.techmate.techmate.Service.RoleService;

import jakarta.transaction.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private UsuarioRoleRepository usuarioRoleRepository;

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private RoleRepository roleRepository;

    // this Method is used to convert entity DTO
    private RoleDTO convertToDTO(Role rol) {
        RoleDTO dto = new RoleDTO();

        dto.setRoleId(rol.getRoleId());
        dto.setName(rol.getNombre());

        return dto;
    }

    private Role convertToEntity(RoleDTO roleDTO) {

        Role role = new Role();
        role.setRoleId(roleDTO.getRoleId());
        role.setNombre(roleDTO.getName());
        return role;
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {

        Role rol = convertToEntity(roleDTO);
        rol = roleRepository.save(rol);
        return convertToDTO(rol);
    }

    @Override
    public RoleDTO getRoleById(int roleId) {
        Role rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        return convertToDTO(rol);
    }

    @Override
    public RoleDTO updateRole(int roleId, RoleDTO roleDTO) {
        Role rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        // Actualizar los valores del rol existente con los del DTO
        rol.setNombre(roleDTO.getName());

        // Guardar el rol actualizado
        Role updatedRole = roleRepository.save(rol);

        return convertToDTO(updatedRole);
    }



    @Override
    public List<RoleDTO> getAllRole() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public String getRoleNameById(int roleId) {

        Role rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        return rol != null ? rol.getNombre() : null;
    }

    @Override
    @Transactional
    public void cleanupRoleAssociations(int roleId) {
        // Verifica si el rol existe en la base de datos
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado con id: " + roleId));

        // Eliminar las asociaciones de usuarios en UsuarioRole
        List<UsuarioRole> usuarioRoles = usuarioRoleRepository.findByRole(role);
        usuarioRoleRepository.deleteAll(usuarioRoles);

        // Eliminar las asociaciones de materiales en Materials
        List<Materials> materials = materialsRepository.findByRole(role);
        for (Materials material : materials) {
            material.setRole(null); // Desasociar el rol
        }
        materialsRepository.saveAll(materials);

        // Eliminar todos los materiales que tienen el rol desasociado (role == null)
        List<Materials> materialsWithoutRole = materialsRepository.findByRoleIsNull();
        materialsRepository.deleteAll(materialsWithoutRole);

        // Finalmente, eliminar el rol
        roleRepository.delete(role);
    }

}
