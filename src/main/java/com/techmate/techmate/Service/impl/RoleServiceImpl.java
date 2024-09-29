package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.RoleDTO;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Repository.RoleRepository;
import com.techmate.techmate.Service.RoleService;

@Service
public class RoleServiceImpl implements RoleService{

    @Autowired
    RoleRepository roleRepository;

    //this Method is used to convert entity  DTO
    private RoleDTO convertToDTO(Role rol){
        RoleDTO dto = new RoleDTO();

        dto.setRoleId(rol.getRoleId());
        dto.setName(rol.getNombre());

        return dto;
    }

    private Role convertToEntity(RoleDTO roleDTO){

        Role role= new Role();
        role.setRoleId(roleDTO.getRoleId());
        role.setNombre(roleDTO.getName());
        return role;
    }



    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        
        Role rol= convertToEntity(roleDTO);
        rol = roleRepository.save(rol);
        return convertToDTO(rol);
    }

    @Override
    public RoleDTO getRoleById(int roleId) {
        Role rol= roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        return convertToDTO(rol);
    }

    @Override
    public RoleDTO updateRole(int roleId, RoleDTO roleDTO) {
        Role rol= roleRepository.findById(roleId)
            .orElseThrow(() ->  new RuntimeException("Role not found with ID: " + roleId));

             // Actualizar los valores del rol existente con los del DTO
        rol.setNombre(roleDTO.getName());

        // Guardar el rol actualizado
        Role updatedRole = roleRepository.save(rol);

        return convertToDTO(updatedRole);
    }

    @Override
    public void deleteRole(int roleId) {
       
        Role rol = roleRepository.findById(roleId)
        .orElseThrow(() ->  new RuntimeException("Role not found with ID: " + roleId));

            roleRepository.delete(rol);
    }

    @Override
    public List<RoleDTO> getAllRole() {
       return roleRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

    public String getRoleNameById(int roleId){

        Role rol= roleRepository.findById(roleId) .orElseThrow(() ->  new RuntimeException("Role not found with ID: " + roleId));

        return rol != null ? rol.getNombre() : null;
    }
    
    
}
