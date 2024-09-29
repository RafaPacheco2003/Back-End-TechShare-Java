package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.SubCategories;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.RoleRepository;
import com.techmate.techmate.Repository.SubCategoriesRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Security.TokenUtils;
import com.techmate.techmate.Service.MaterialsService;
import com.techmate.techmate.Service.RoleService;
import com.techmate.techmate.Service.SubCategoriesService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@Service
public class MaterialsServiceImp implements MaterialsService {

    @Autowired
private UsuarioRepository usuarioRepository; // Asegúrate de que este repositorio esté inyectado

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private SubCategoriesService subCategoriesService;

    @Autowired
    private SubCategoriesRepository subCategoriesRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    // Método para convertir de entidad a DTO
    private MaterialsDTO convertToDTO(Materials materials) {
        MaterialsDTO dto = new MaterialsDTO();
        dto.setMaterialsId(materials.getMaterialsId());
        dto.setImagePath(materials.getImagePath());
        dto.setName(materials.getName());
        dto.setDescription(materials.getDescription());
        dto.setPrice(materials.getPrice());

        // Asignar valores directamente, ya que son int (no pueden ser null)
        dto.setStock(materials.getStock());
        dto.setBorrowable_stock(materials.getBorrowable_stock());

        dto.setSubCategoryId(materials.getSubCategory().getSubCategoryId());
        dto.setSubCategoryName(
                subCategoriesService.getSubCategoryNameById(materials.getSubCategory().getSubCategoryId()));

        dto.setRolId(materials.getRole().getRoleId());
        dto.setRolName(roleService.getRoleNameById(materials.getRole().getRoleId()));

        return dto;
    }

    private Materials convertToEntity(MaterialsDTO materialsDTO) {
        Materials materials = new Materials();
        materials.setImagePath(materialsDTO.getImagePath());
        materials.setName(materialsDTO.getName());
        materials.setDescription(materialsDTO.getDescription());
        materials.setPrice(materialsDTO.getPrice());

        // Asignar valores predeterminados a stock y borrowable_stock
        materials.setStock(0); // Establecer stock a 0
        materials.setBorrowable_stock(0); // Establecer borrowable_stock a 0

        // Buscar la subcategoría por su ID y asignarla
        SubCategories subCategories = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Subcategoría no encontrada con ID: " + materialsDTO.getSubCategoryId()));
        materials.setSubCategory(subCategories);

        // Buscar el rol por su ID y asignarlo
        Role role = roleRepository.findById(materialsDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + materialsDTO.getRolId()));
        materials.setRole(role);

        return materials;
    }
    

    @Override
    public MaterialsDTO createMaterials(MaterialsDTO materialsDTO) {
        Materials materials = convertToEntity(materialsDTO);
        materials = materialsRepository.save(materials);

        return convertToDTO(materials);
    }


    @Override
    public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO) {

        
        Materials materials = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new RuntimeException("Materials not found with ID: " + materialsId));

        // Se actualizan solo los campos que se permiten modificar
        materials.setName(materialsDTO.getName());
        materials.setDescription(materialsDTO.getDescription());
        materials.setPrice(materialsDTO.getPrice());

        // No se permite modificar stock y borrowable_stock
        // materials.setStock(materialsDTO.getStock()); // Comentado para no permitir la
        // modificación
        // materials.setBorrowable_stock(materialsDTO.getBorrowable_stock()); //
        // Comentado para no permitir la modificación

        // Buscar y asignar la subcategoría y el rol
        SubCategories subCategories = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Subcategoría no encontrada con ID: " + materialsDTO.getSubCategoryId()));
        materials.setSubCategory(subCategories);

        Role role = roleRepository.findById(materialsDTO.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + materialsDTO.getRolId()));
        materials.setRole(role);

        Materials updatedMaterials = materialsRepository.save(materials);
        return convertToDTO(updatedMaterials);
    }

    @Override
    public void deleteMaterials(int materialsId) {
        Materials materials = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new RuntimeException("Role not found with ID: " + materialsId));

        materialsRepository.delete(materials);

    }

    @Override
    public List<MaterialsDTO> getAllMaterials() {
        return materialsRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MaterialsDTO getMaterialsById(int materialsId) {
        Materials materials = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new RuntimeException("Materials not found with ID: " + materialsId));

        return convertToDTO(materials);
    }




    
    @Override
    public List<MaterialsDTO> getAllMaterialsByRole() {
        
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // Obtenemos el email del usuario autenticado
    
        // Encontrar el usuario por su email
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el email: " + userEmail));
    
        // Filtrar los materiales según los roles del usuario
        List<Materials> materials = materialsRepository.findByRoleNombreIn(
                usuario.getRoles().stream()
                       .map(Role::getNombre) // Obtener los nombres de los roles
                       .collect(Collectors.toList())
        );
    
        // Convertir la lista de Materials a DTO
        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    



    
}
