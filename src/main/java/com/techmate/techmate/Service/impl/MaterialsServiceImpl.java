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

/**
 * Implementación del servicio de gestión de materiales.
 * 
 * RESPONSABILIDADES:
 * 1. Gestionar el ciclo de vida completo de los materiales (CRUD)
 * 2. Coordinar el almacenamiento de imágenes asociadas
 * 3. Mantener la relación entre materiales, roles y subcategorías
 * 4. Convertir entre entidades JPA (Materials) y DTOs (MaterialsDTO)
 * 5. Validar reglas de negocio (nombres únicos, stock coherente, etc.)
 * 
 * FLUJO TÍPICO:
 * Controller → MaterialsService → MaterialsRepository → Base de Datos
 *                    ↓
 *           ImageStorageStrategy (para archivos)
 *                    ↓
 *           Validation (reglas de negocio)
 * 
 * ARQUITECTURA:
 * - Patrón Service: separa lógica de negocio de controllers
 * - Patrón Strategy: delega almacenamiento de imágenes a implementaciones intercambiables
 * - Patrón DTO: previene exposición directa de entidades JPA al cliente
 * 
 * @author TechMate Team
 * @version 1.0
 * @since 2025-10-02
 */
@Service
public class MaterialsServiceImpl implements MaterialsService {

    // ==================== DEPENDENCIAS INYECTADAS ====================
    
    /**
     * Repository para acceso a datos de materiales.
     * Provee métodos CRUD + consultas personalizadas.
     */
    @Autowired
    private MaterialsRepository materialsRepository;

    /**
     * Servicio de subcategorías (para obtener nombres, validar existencia).
     */
    @Autowired
    private SubCategoriesService subCategoriesService;

    /**
     * Repository de subcategorías (acceso directo a BD).
     */
    @Autowired
    private SubCategoriesRepository subCategoriesRepository;

    /**
     * Servicio de roles (para validar permisos y obtener nombres).
     */
    @Autowired
    private RoleService roleService;

    /**
     * Repository de roles (acceso directo a BD).
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Estrategia de validación de imágenes.
     * Valida extensiones permitidas (.jpg, .png, .gif, etc.).
     */
    @Autowired
    ImageValidationStrategy imageValidationStrategy;

    /**
     * Estrategia de almacenamiento de imágenes.
     * Puede ser FileSystemImageStorage u OptimizedImageStorage.
     * Spring inyecta la implementación por defecto o la especificada con @Qualifier.
     */
    @Autowired
    ImageStorageStrategy imageStorageStrategy;

    // ==================== MÉTODOS DE CONVERSIÓN (ENTITY ↔ DTO) ====================
    
    /**
     * Convierte una entidad Materials (JPA) a MaterialsDTO (transferencia).
     * 
     * PROPÓSITO:
     * - Preparar datos para enviar al frontend (JSON)
     * - Evitar exponer entidades JPA directamente (previene lazy loading issues)
     * - Aplanar relaciones complejas (ej: lista de RoleMaterials → lista de IDs)
     * 
     * TRANSFORMACIONES:
     * - Materials.subCategory (objeto) → subCategoryId + subCategoryName
     * - Materials.roleMaterials (List<RoleMaterials>) → roleIds[] + roleNames[]
     * 
     * @param materials Entidad JPA a convertir
     * @return MaterialsDTO listo para serializar a JSON
     */
    private MaterialsDTO convertToDTO(Materials materials) {
        MaterialsDTO dto = new MaterialsDTO();
        
        // Datos básicos del material
        dto.setMaterialsId(materials.getMaterialsId());
        dto.setImagePath(materials.getImagePath());
        dto.setName(materials.getName());
        dto.setDescription(materials.getDescription());
        dto.setPrice(materials.getPrice());

        // Stock (int primitivo, no puede ser null)
        dto.setStock(materials.getStock());
        dto.setBorrowable_stock(materials.getBorrowable_stock());

        // Subcategoría: extraer ID y nombre
        // NOTA: Asumimos que SubCategory siempre existe (NOT NULL en BD)
        dto.setSubCategoryId(materials.getSubCategory().getSubCategoryId());
        dto.setSubCategoryName(
                subCategoriesService
                        .getSubCategoryNameById(materials.getSubCategory().getSubCategoryId()));

        // Roles: convertir List<RoleMaterials> → List<Integer> roleIds y List<String> roleNames
        // Usamos streams para transformar eficientemente
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

    /**
     * Convierte un MaterialsDTO (desde el frontend) a entidad Materials (JPA).
     * 
     * PROPÓSITO:
     * - Preparar datos para persistir en la base de datos
     * - Resolver relaciones (cargar SubCategories y Roles desde BD)
     * - Inicializar colecciones y relaciones bidireccionales
     * 
     * VALIDACIONES:
     * - Verifica que la subcategoría existe (lanza RuntimeException si no)
     * - Verifica que todos los roles existen
     * 
     * REGLA DE NEGOCIO:
     * - borrowable_stock se inicializa igual que stock (todo disponible para préstamo)
     * - Si stock = 0, ambos se setean a 0
     * 
     * @param materialsDTO DTO recibido del frontend
     * @return Materials entidad JPA lista para save()
     * @throws RuntimeException si subcategoría o algún rol no existe
     */
    private Materials convertToEntity(MaterialsDTO materialsDTO) {
        System.out.println("Convirtiendo MaterialsDTO a Materials: " + materialsDTO);

        Materials materials = new Materials();
        
        // Datos básicos
        materials.setImagePath(materialsDTO.getImagePath());
        materials.setName(materialsDTO.getName());
        materials.setDescription(materialsDTO.getDescription());
        materials.setPrice(materialsDTO.getPrice());

        // REGLA DE NEGOCIO: Stock y borrowable_stock
        // Al crear un material, todo el stock está disponible para préstamo
        if (materialsDTO.getStock() == 0) {
            materials.setStock(0);
            materials.setBorrowable_stock(0);
        } else {
            materials.setStock(materialsDTO.getStock());
            materials.setBorrowable_stock(materialsDTO.getStock()); // Inicialmente todo disponible
        }

        // RELACIÓN: Subcategoría (ManyToOne)
        // Buscar subcategoría en BD, lanzar excepción si no existe
        SubCategories subCategories = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException("Subcategoría no encontrada con ID: "
                        + materialsDTO.getSubCategoryId()));
        materials.setSubCategory(subCategories);

        // RELACIÓN: Roles (ManyToMany via RoleMaterials)
        // Crear lista de RoleMaterials (tabla intermedia)
        List<RoleMaterials> roleMaterialsList = new ArrayList<>();
        
        for (Integer roleId : materialsDTO.getRoleIds()) {
            // Buscar rol en BD, lanzar excepción si no existe
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));

            // Crear registro en tabla intermedia role_materials
            RoleMaterials roleMaterials = new RoleMaterials();
            roleMaterials.setRole(role);           // FK a role
            roleMaterials.setMaterials(materials); // FK a materials (bidireccional)
            roleMaterialsList.add(roleMaterials);
        }

        // Setear la lista completa de roles
        materials.setRoleMaterials(roleMaterialsList);
        
        return materials;
    }

    // ==================== OPERACIONES CRUD ====================
    
    /**
     * Crea un nuevo material en el sistema.
     * 
     * FLUJO:
     * 1. Validar que no exista otro material con el mismo nombre (constraint de negocio)
     * 2. Validar y guardar imagen si se proporciona
     * 3. Convertir DTO a entidad
     * 4. Persistir en base de datos
     * 5. Retornar DTO del material creado
     * 
     * VALIDACIONES:
     * - Nombre único (no puede haber dos materiales con igual nombre)
     * - Extensión de imagen válida (.jpg, .png, .gif, etc.)
     * - Subcategoría existe
     * - Todos los roles existen
     * 
     * TRANSACCIONALIDAD:
     * - Si alguna operación falla, todo se revierte (atomicidad)
     * - La imagen se guarda ANTES de la BD (para evitar referencias rotas)
     * 
     * @param materialsDTO Datos del material a crear (desde frontend)
     * @param image Archivo de imagen (opcional, puede ser null)
     * @return MaterialsDTO del material creado (con ID asignado)
     * @throws IllegalArgumentException si ya existe material con ese nombre
     * @throws RuntimeException si subcategoría o roles no existen, o imagen inválida
     */
    @Override
    public MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile image) {
        // VALIDACIÓN 1: Nombre único
        // Previene duplicados que podrían confundir al inventario
        if (materialsDTO.getName() != null && materialsRepository.findByName(materialsDTO.getName()) != null) {
            throw new IllegalArgumentException("Ya existe un material con el nombre: " + materialsDTO.getName());
        }

        // VALIDACIÓN 2 Y ALMACENAMIENTO: Imagen (si se proporciona)
        if (image != null && !image.isEmpty()) {
            // Obtener nombre original para validar extensión
            String imagePath = image.getOriginalFilename();
            
            // Validar extensión (.jpg, .png, .gif, etc.)
            imageValidationStrategy.validate(imagePath);

            // Guardar imagen en storage (filesystem, S3, etc.)
            // Retorna ruta relativa: "uuid-1234.jpg"
            String savedImagePath = imageStorageStrategy.saveImage(image);
            
            // Actualizar DTO con la ruta guardada
            materialsDTO.setImagePath(savedImagePath);
        }

        // PERSISTENCIA: Convertir DTO → Entity y guardar en BD
        Materials materials = convertToEntity(materialsDTO);
        materials = materialsRepository.save(materials); // INSERT en BD

        // RESPUESTA: Convertir Entity → DTO para retornar al cliente
        return convertToDTO(materials);
    }

    @Override
    public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile image) {
        // Buscar el material existente por su ID
        Materials existingMaterial = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + materialsId));

        Integer stockMaterials= existingMaterial.getStock();
        Integer stockBorrow= existingMaterial.getBorrowable_stock();

        // Actualizar el nombre si es proporcionado (sin verificación de duplicado)
        if (materialsDTO.getName() != null) {
            existingMaterial.setName(materialsDTO.getName());
        }

        // Actualizar los demás valores del material
        existingMaterial.setDescription(materialsDTO.getDescription());
        existingMaterial.setPrice(materialsDTO.getPrice());
        existingMaterial.setStock(stockMaterials);
        existingMaterial.setBorrowable_stock(stockBorrow);

        // Manejo de subcategoría
        SubCategories subCategory = subCategoriesRepository.findById(materialsDTO.getSubCategoryId())
                .orElseThrow(() -> new RuntimeException(
                        "Subcategoría no encontrada con ID: " + materialsDTO.getSubCategoryId()));
        existingMaterial.setSubCategory(subCategory);

        // Manejo de la imagen (si se proporciona una nueva)
        if (image != null && !image.isEmpty()) {
            String imagePath = image.getOriginalFilename(); // Obtener el nombre original de la imagen
            imageValidationStrategy.validate(imagePath); // Validar la extensión

            // Guardar la imagen y actualizar la ruta
            String savedImagePath = imageStorageStrategy.saveImage(image);
            existingMaterial.setImagePath(savedImagePath);
        }

        // Actualizar roles asociados al material
        List<RoleMaterials> updatedRoleMaterials = new ArrayList<>();
        for (Integer roleId : materialsDTO.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));

            RoleMaterials roleMaterials = new RoleMaterials();
            roleMaterials.setRole(role);
            roleMaterials.setMaterials(existingMaterial);
            updatedRoleMaterials.add(roleMaterials);
        }

        // Limpiar la lista de roles actuales y agregar los actualizados
        existingMaterial.getRoleMaterials().clear();
        existingMaterial.getRoleMaterials().addAll(updatedRoleMaterials);

        // Guardar el material actualizado en el repositorio
        Materials updatedMaterial = materialsRepository.save(existingMaterial);

        // Convertir el material actualizado a DTO y devolverlo
        return convertToDTO(updatedMaterial);
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
