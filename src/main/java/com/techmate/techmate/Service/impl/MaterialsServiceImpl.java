package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.SubCategories;

import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.RoleRepository;
import com.techmate.techmate.Repository.SubCategoriesRepository;

import com.techmate.techmate.Service.MaterialsService;
import com.techmate.techmate.Service.RoleService;
import com.techmate.techmate.Service.SubCategoriesService;
import com.techmate.techmate.Validation.ImageValidationStrategy;

@Service
public class MaterialsServiceImpl implements MaterialsService {

        /*
         * Repository
         */
        @Autowired
        private MaterialsRepository materialsRepository;

        @Autowired
        private SubCategoriesService subCategoriesService;

        @Autowired
        private SubCategoriesRepository subCategoriesRepository;

        /*
         * Services
         */
        @Autowired
        private RoleService roleService;

        @Autowired
        private RoleRepository roleRepository;

        /*
         * Validations
         */

        @Autowired
        ImageValidationStrategy imageValidationStrategy;

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
                                subCategoriesService
                                                .getSubCategoryNameById(materials.getSubCategory().getSubCategoryId()));

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
                                                "Subcategoría no encontrada con ID: "
                                                                + materialsDTO.getSubCategoryId()));
                materials.setSubCategory(subCategories);

                // Buscar el rol por su ID y asignarlo
                Role role = roleRepository.findById(materialsDTO.getRolId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Rol no encontrado con ID: " + materialsDTO.getRolId()));
                materials.setRole(role);

                return materials;
        }

        @Override
        public MaterialsDTO createMaterials(MaterialsDTO materialsDTO) {
                
                // Usa EL PATRON DE DISEÑO estrategia de validación para validar el imagePath
                imageValidationStrategy.validate(materialsDTO.getImagePath());

                Materials materials = convertToEntity(materialsDTO);
                materials = materialsRepository.save(materials);

                return convertToDTO(materials);
        }

        @Override
        public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO) {
                Materials materials = materialsRepository.findById(materialsId)
                                .orElseThrow(() -> new RuntimeException("Materials not found with ID: " + materialsId));

                //Is ussed the patron de diseño strategy validation to validar the imagePäth
                imageValidationStrategy.validate(materialsDTO.getImagePath());
                

                // Actualizar solo los campos que se permiten modificar
                materials.setName(materialsDTO.getName());
                materials.setImagePath(materialsDTO.getImagePath());
                materials.setDescription(materialsDTO.getDescription());
                materials.setPrice(materialsDTO.getPrice());

                // Buscar y asignar la subcategoría y el rol
                SubCategories subCategories = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Subcategoría no encontrada con ID: "
                                                                + materialsDTO.getSubCategoryId()));
                materials.setSubCategory(subCategories);

                Role role = roleRepository.findById(materialsDTO.getRolId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Rol no encontrado con ID: " + materialsDTO.getRolId()));
                materials.setRole(role);

                Materials updatedMaterials = materialsRepository.save(materials);
                return convertToDTO(updatedMaterials);
        }

        @Override
public void deleteMaterials(int materialsId) {
    Materials materials = materialsRepository.findById(materialsId)
            .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + materialsId));
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
        public String getMaterialsNameById(int materialId) {
            // Busca el material por su ID en el repositorio y obtiene el nombre si se encuentra
            return materialsRepository.findById(materialId)
                .map(Materials::getName) // Obtiene el nombre del material si se encuentra
                .orElse(null); // Devuelve null si no se encuentra el material
        }
        
        


}
