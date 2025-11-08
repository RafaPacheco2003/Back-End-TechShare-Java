package com.techmate.techmate.service.role.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.dto.RoleDTO;
import com.techmate.techmate.entity.Role;

@Component
public class RoleMapper {

    public RoleDTO toDTO(Role role) {
        if (role == null) return null;
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getRoleId());
        dto.setName(role.getNombre());
        return dto;
    }

    public Role toEntity(RoleDTO dto) {
        if (dto == null) return null;
        Role role = new Role();
        role.setRoleId(dto.getRoleId());
        role.setNombre(dto.getName());
        return role;
    }
}

