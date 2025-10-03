package com.techmate.techmate.Service.role.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.techmate.techmate.Service.role.mapper.RoleMapper;
import com.techmate.techmate.dto.RoleDTO;
import com.techmate.techmate.repository.RoleRepository;

@Component
public class RoleQueryService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleQueryService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream().filter(r -> r.getRoleId() != 1).map(roleMapper::toDTO).collect(Collectors.toList());
    }

    public RoleDTO getById(int id) {
        return roleRepository.findById(id).map(roleMapper::toDTO).orElse(null);
    }
}
