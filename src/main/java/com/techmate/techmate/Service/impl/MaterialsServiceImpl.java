package com.techmate.techmate.Service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.SubCategories;
import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
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

        @Autowired
        ImageStorageStrategy imageStorageStrategy;

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
        public MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile image) {

                // Verificar si ya existe otro material con el mismo nombre (opcional)
                if (materialsDTO.getName() != null &&
                                materialsRepository.findByName(materialsDTO.getName()) != null) {
                        throw new IllegalArgumentException(
                                        "Ya existe un material con el nombre: " + materialsDTO.getName());
                }

                

                String imagePath = image.getOriginalFilename(); // Obtener el nombre original de la imagen
                imageValidationStrategy.validate(imagePath); // Validación de la extensión

                // Guardar la imagen y obtener la ruta
                String savedImagePath = imageStorageStrategy.saveImage(image);

                materialsDTO.setImagePath(savedImagePath);

                Materials materials = convertToEntity(materialsDTO);
                materials = materialsRepository.save(materials);

                return convertToDTO(materials);
        }

        @Override
        public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile image) {
                // Buscar el material por ID y lanzar excepción si no se encuentra
                Materials materials = materialsRepository.findById(materialsId)
                                .orElseThrow(() -> new RuntimeException("Materials not found with ID: " + materialsId));

                // Verificar si ya existe otro material con el mismo nombre (opcional)
                if (materialsDTO.getName() != null &&
                                materialsRepository.findByName(materialsDTO.getName()) != null) {
                        throw new IllegalArgumentException(
                                        "Ya existe un material con el nombre: " + materialsDTO.getName());
                }

                if (materials != null) {
                        materials.setName(materialsDTO.getName());
                        materials.setDescription(materialsDTO.getDescription());
                        materials.setPrice(materialsDTO.getPrice());

                        // Si se proporciona una nueva imagen, validar y guardar la nueva imagen
                        if (image != null && !image.isEmpty()) {
                                String oldImagePath = materials.getImagePath();

                                // Eliminar la imagen antigua si existe
                                if (oldImagePath != null && !oldImagePath.isEmpty()) {
                                        imageStorageStrategy.deleteImage(oldImagePath);
                                }

                                // Validar la nueva imagen usando la estrategia de validación
                                String newImagePath = image.getOriginalFilename();
                                imageValidationStrategy.validate(newImagePath); // Validar extensión o formato

                                // Guardar la nueva imagen y establecer su ruta en la entidad
                                newImagePath = imageStorageStrategy.saveImage(image);
                                materials.setImagePath(newImagePath); // Actualizar la ruta de la imagen
                        }
                        // Buscar y asignar la subcategoría y el rol
                        SubCategories subCategories = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                                        .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada con ID: "
                                                        + materialsDTO.getSubCategoryId()));
                        materials.setSubCategory(subCategories);

                        Role role = roleRepository.findById(materialsDTO.getRolId())
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Rol no encontrado con ID: " + materialsDTO.getRolId()));
                        materials.setRole(role);

                        // Guardar la entidad actualizada en la base de datos
                        Materials updatedMaterials = materialsRepository.save(materials);
                        return convertToDTO(updatedMaterials); // Devolver el DTO del material actualizado
                }

                return null;

        }

        @Override
        public void deleteMaterials(int materialsId) {
            // Buscar el material por ID
            Materials materials = materialsRepository.findById(materialsId)
                    .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + materialsId));
        
            // Si se encuentra el material, eliminar la imagen si existe
            String imagePath = materials.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia para eliminar la imagen
            }
        
            // Eliminar el material de la base de datos
            materialsRepository.deleteById(materialsId);
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
                // Busca el material por su ID en el repositorio y obtiene el nombre si se
                // encuentra
                return materialsRepository.findById(materialId)
                                .map(Materials::getName) // Obtiene el nombre del material si se encuentra
                                .orElse(null); // Devuelve null si no se encuentra el material
        }

}
