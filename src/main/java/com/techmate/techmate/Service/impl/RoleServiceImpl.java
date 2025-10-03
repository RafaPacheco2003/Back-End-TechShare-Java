package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techmate.techmate.Service.RoleService;
import com.techmate.techmate.dto.RoleDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.RoleMaterials;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.UsuarioRole;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.RoleMaterialsRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.UsuarioRoleRepository;

import jakarta.transaction.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private final UsuarioRoleRepository usuarioRoleRepository;
    private final MaterialsRepository materialsRepository;
    private final RoleMaterialsRepository roleMaterialsRepository;
    private final RoleRepository roleRepository;
    private final com.techmate.techmate.Service.role.mapper.RoleMapper roleMapper;
    private final com.techmate.techmate.Service.role.validator.RoleValidator roleValidator;
    private final com.techmate.techmate.Service.role.query.RoleQueryService roleQueryService;
    private final com.techmate.techmate.Service.role.manager.RoleAssociationManager roleAssociationManager;

    public RoleServiceImpl(UsuarioRoleRepository usuarioRoleRepository,
                           MaterialsRepository materialsRepository,
                           RoleMaterialsRepository roleMaterialsRepository,
                           RoleRepository roleRepository,
                           com.techmate.techmate.Service.role.mapper.RoleMapper roleMapper,
                           com.techmate.techmate.Service.role.validator.RoleValidator roleValidator,
                           com.techmate.techmate.Service.role.query.RoleQueryService roleQueryService,
                           com.techmate.techmate.Service.role.manager.RoleAssociationManager roleAssociationManager) {
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.materialsRepository = materialsRepository;
        this.roleMaterialsRepository = roleMaterialsRepository;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.roleValidator = roleValidator;
        this.roleQueryService = roleQueryService;
        this.roleAssociationManager = roleAssociationManager;
    }

    // this Method is used to convert entity DTO
    private RoleDTO convertToDTO(Role rol) {
        return roleMapper.toDTO(rol);
    }

    private Role convertToEntity(RoleDTO roleDTO) {
        return roleMapper.toEntity(roleDTO);
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {

        roleValidator.validateUniqueName(roleDTO.getName());
        Role rol = convertToEntity(roleDTO);
        rol = roleRepository.save(rol);
        return convertToDTO(rol);
    }

    @Override
    public RoleDTO getRoleById(int roleId) {
    return roleQueryService.getById(roleId);
    }

    @Override
    public RoleDTO updateRole(int roleId, RoleDTO roleDTO) {
    Role rol = roleRepository.findById(roleId)
        .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));
    roleValidator.validateUniqueName(roleDTO.getName());
    rol.setNombre(roleDTO.getName());
    Role updatedRole = roleRepository.save(rol);
    return convertToDTO(updatedRole);
    }

    @Override
    public List<RoleDTO> getAllRole() {
    return roleQueryService.getAllRoles();
    }

    public String getRoleNameById(int roleId) {

        Role rol = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + roleId));

        return rol != null ? rol.getNombre() : null;
    }

    @Override
    @Transactional
    public void cleanupRoleAssociations(int roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role no encontrado con id: " + roleId));
        roleAssociationManager.cleanupRoleAssociations(role);
        roleRepository.delete(role);
    }

}
