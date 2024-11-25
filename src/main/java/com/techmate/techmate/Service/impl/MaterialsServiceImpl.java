package com.techmate.techmate.Service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.DTO.MaterialsDTO;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.RoleMaterials;
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

        // Obtener los roles asociados
        List<Integer> roleIds = materials.getRoleMaterials().stream()
                .map(roleMaterial -> roleMaterial.getRole().getRoleId())
                .collect(Collectors.toList());

        List<String> roleNames = materials.getRoleMaterials().stream()
                .map(roleMaterial -> roleService.getRoleNameById(roleMaterial.getRole().getRoleId()))
                .collect(Collectors.toList());

        dto.setRoleIds(roleIds);
        dto.setRoleNames(roleNames);

        return dto;
    }

    private Materials convertToEntity(MaterialsDTO materialsDTO) {
        System.out.println("Convirtiendo MaterialsDTO a Materials: " + materialsDTO);

        Materials materials = new Materials();
        materials.setImagePath(materialsDTO.getImagePath());
        materials.setName(materialsDTO.getName());
        materials.setDescription(materialsDTO.getDescription());
        materials.setPrice(materialsDTO.getPrice());

        // Asignar valores predeterminados
        materials.setStock(0);
        materials.setBorrowable_stock(0);

        // Verificar subcategoría
        SubCategories subCategories = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada con ID: "
                        + materialsDTO.getSubCategoryId()));
        materials.setSubCategory(subCategories);

        // Convertir roles
        List<RoleMaterials> roleMaterialsList = new ArrayList<>();
        for (Integer roleId : materialsDTO.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));

            // Solo añadir a la lista si el rol existe
            RoleMaterials roleMaterials = new RoleMaterials();
            roleMaterials.setRole(role);
            roleMaterials.setMaterials(materials);
            roleMaterialsList.add(roleMaterials);
        }

        materials.setRoleMaterials(roleMaterialsList);
        return materials;
    }

    @Override
public MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile image) {
    List<Integer> listRole = materialsDTO.getRoleIds();
    materialsDTO.setRoleIds(listRole);

    // Verificar si ya existe otro material con el mismo nombre
    if (materialsDTO.getName() != null && materialsRepository.findByName(materialsDTO.getName()) != null) {
        throw new IllegalArgumentException("Ya existe un material con el nombre: " + materialsDTO.getName());
    }

    // Si la imagen no es nula ni vacía, proceder con su validación y almacenamiento
    if (image != null && !image.isEmpty()) {
        String imagePath = image.getOriginalFilename(); // Obtener el nombre original de la imagen
        imageValidationStrategy.validate(imagePath); // Validación de la extensión

        // Guardar la imagen y obtener la ruta
        String savedImagePath = imageStorageStrategy.saveImage(image);
        materialsDTO.setImagePath(savedImagePath);
    }

    // Convertir el DTO a entidad y guardar
    Materials materials = convertToEntity(materialsDTO);
    materials = materialsRepository.save(materials);

    return convertToDTO(materials);
}


    @Override
    public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile image) {
        // Buscar el material por ID
        Materials materials = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new RuntimeException("Materials not found with ID: " + materialsId));

        // Actualizar los campos básicos
        materials.setName(materialsDTO.getName());
        materials.setDescription(materialsDTO.getDescription());
        materials.setPrice(materialsDTO.getPrice());

        // Manejar la actualización de la imagen
        if (image != null && !image.isEmpty()) {
            String oldImagePath = materials.getImagePath();
            if (oldImagePath != null && !oldImagePath.isEmpty()) {
                imageStorageStrategy.deleteImage(oldImagePath);
            }
            String newImagePath = imageStorageStrategy.saveImage(image);
            materials.setImagePath(newImagePath);
        }

        // Validar que el ID de la subcategoría no sea nulo
        Integer subCategoryId = materialsDTO.getSubCategoryId();
        if (subCategoryId == null) {
            throw new IllegalArgumentException("El ID de la subcategoría no puede ser nulo");
        }

        // Manejar subcategoría
        SubCategories subCategories = subCategoriesRepository.findById(subCategoryId)
                .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada con ID: " + subCategoryId));
        materials.setSubCategory(subCategories);

        // Actualizar la relación RoleMaterials
        materials.getRoleMaterials().clear(); // Eliminar las relaciones antiguas

        // Validar que la lista de IDs de roles no sea nula ni vacía
        if (materialsDTO.getRoleIds() == null || materialsDTO.getRoleIds().isEmpty()) {
            throw new IllegalArgumentException("La lista de roles no puede ser nula o vacía");
        }

        // Crear una referencia final a materials
        final Materials finalMaterials = materials;

        // Mapear los IDs de roles a nuevas instancias de RoleMaterials
        List<RoleMaterials> newRoleMaterials = materialsDTO.getRoleIds().stream()
                .map(roleId -> {
                    // Validar que el ID del rol no sea nulo
                    if (roleId == null) {
                        throw new IllegalArgumentException("El ID del rol no puede ser nulo");
                    }

                    // Buscar el rol por ID
                    Role role = roleRepository.findById(roleId)
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));

                    // Retornar una nueva instancia de RoleMaterials
                    return new RoleMaterials(null, role, finalMaterials);
                })
                .collect(Collectors.toList());

        // Asignar los nuevos roles al material
        materials.setRoleMaterials(newRoleMaterials);

        // Guardar el material actualizado
        materials = materialsRepository.save(materials);

        // Retornar el DTO actualizado
        return convertToDTO(materials);
    }

    @Override
    public void deleteMaterials(int materialsId) {
        // Buscar el material por ID
        Materials materials = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new RuntimeException(
                        "Material no encontrado con ID: " + materialsId));

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

    @Override
    public List<MaterialsDTO> getAllMaterialsSortedByPrice(boolean ascending) {
        List<Materials> materials;

        if (ascending) {
            materials = materialsRepository.findAllByOrderByPriceAsc(); // Obtiene materiales en orden
                                                                        // ascendente
        } else {
            materials = materialsRepository.findAllByOrderByPriceDesc(); // Obtiene materiales en orden
                                                                         // descendente
        }

        return materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
