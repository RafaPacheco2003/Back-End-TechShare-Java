package com.techmate.techmate.Service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.techmate.techmate.ImageStorage.ImageStorageStrategy;
import com.techmate.techmate.Service.MaterialsService;
import com.techmate.techmate.Validation.ImageValidationStrategy;
import com.techmate.techmate.dto.MaterialsDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.RoleMaterials;
import com.techmate.techmate.entity.SubCategories;
import com.techmate.techmate.event.MaterialLowStockEvent;
import com.techmate.techmate.exception.BusinessException;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.SubCategoriesRepository;

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
    private final MaterialsRepository materialsRepository;
    private final SubCategoriesRepository subCategoriesRepository;
    private final RoleRepository roleRepository;
    private final ImageValidationStrategy imageValidationStrategy;
    private final ImageStorageStrategy imageStorageStrategy;
    private final com.techmate.techmate.Service.materials.mapper.MaterialsMapper materialsMapper;
    private final com.techmate.techmate.Service.materials.validator.MaterialsValidator materialsValidator;
    private final com.techmate.techmate.Service.materials.manager.MaterialsStockManager materialsStockManager;
    private final com.techmate.techmate.Service.materials.query.MaterialsQueryService materialsQueryService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Umbral de stock bajo (Low Stock Threshold).
     * Cuando el stock de un material cae por debajo de este valor,
     * se dispara un MaterialLowStockEvent para alertar al sistema.
     */
    private static final int LOW_STOCK_THRESHOLD = 10;

    public MaterialsServiceImpl(
            MaterialsRepository materialsRepository,
            SubCategoriesRepository subCategoriesRepository,
            RoleRepository roleRepository,
            ImageValidationStrategy imageValidationStrategy,
            ImageStorageStrategy imageStorageStrategy,
            com.techmate.techmate.Service.materials.mapper.MaterialsMapper materialsMapper,
            com.techmate.techmate.Service.materials.validator.MaterialsValidator materialsValidator,
            com.techmate.techmate.Service.materials.manager.MaterialsStockManager materialsStockManager,
            com.techmate.techmate.Service.materials.query.MaterialsQueryService materialsQueryService,
            ApplicationEventPublisher eventPublisher) {
        this.materialsRepository = materialsRepository;
        this.subCategoriesRepository = subCategoriesRepository;
        this.roleRepository = roleRepository;
        this.imageValidationStrategy = imageValidationStrategy;
        this.imageStorageStrategy = imageStorageStrategy;
        this.materialsMapper = materialsMapper;
        this.materialsValidator = materialsValidator;
        this.materialsStockManager = materialsStockManager;
        this.materialsQueryService = materialsQueryService;
        this.eventPublisher = eventPublisher;
    }

    // NOTE: Cada dependencia inyectada tiene una responsabilidad clara (SRP):
    // - materialsRepository: acceso a persistencia (DAO)
    // - subCategoriesRepository / roleRepository: resolución de relaciones
    // - imageValidationStrategy / imageStorageStrategy: delegación a estrategias para manejar imágenes (Strategy pattern)
    // - materialsMapper: conversión Entity ↔ DTO (evita lógica de mapping en el service)
    // - materialsValidator: validaciones de negocio (únicas responsabilidades)
    // - materialsStockManager: operaciones relacionadas al stock (aisla concurrencia y transacciones)
    // - materialsQueryService: consultas complejas / proyecciones (separación de consultas)

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
    return materialsMapper.toDTO(materials);
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
        return materialsMapper.toEntity(materialsDTO);
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
    @Transactional
    public MaterialsDTO createMaterials(MaterialsDTO materialsDTO, MultipartFile image) {
        // VALIDACIÓN 1: Nombre único
        materialsValidator.validateUniqueName(materialsDTO.getName());

        // VALIDACIÓN 2 Y ALMACENAMIENTO: Imagen (si se proporciona)
        if (image != null && !image.isEmpty()) {
            // Obtener nombre original para validar extensión
            String imagePath = image.getOriginalFilename();
            
            // Validar extensión (.jpg, .png, .gif, etc.)
            imageValidationStrategy.validate(imagePath);

            // Guardar imagen en storage (filesystem, S3, etc.)
            // Retorna ruta relativa: "uuid-1234.jpg"
            // Observación de diseño: se guarda la imagen antes de persistir en BD para
            // garantizar que la ruta exista cuando la entidad sea retornada. Si la
            // persistencia falla, deberíamos eliminar la imagen almacenada o soportar
            // compensación. Aquí delegamos la política de compensación a la capa
            // de almacenamiento (p.ej. almacenamiento idempotente o lifecycle hooks).
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
    @Transactional
    public MaterialsDTO updateMaterials(int materialsId, MaterialsDTO materialsDTO, MultipartFile image) {
        // Buscar el material existente por su ID
        Materials existingMaterial = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "Material no encontrado con ID: " + materialsId));

    Integer stockMaterials= existingMaterial.getStock();
    Integer stockBorrow= existingMaterial.getBorrowable_stock();

    // Consultar stock actual a través del manager (preparación para lógica de préstamo)
    int available = materialsStockManager.getAvailableStock(existingMaterial.getMaterialsId());
    // Usar la fuente única de verdad para el stock disponible
    stockBorrow = Integer.valueOf(available);

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
        .orElseThrow(() -> new BusinessException("SUBCATEGORY_NOT_FOUND",
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
            .orElseThrow(() -> new BusinessException("ROLE_NOT_FOUND", "Rol no encontrado con ID: " + roleId));

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

        // Verificar si el stock está bajo y publicar evento si es necesario
        checkAndPublishLowStockEvent(updatedMaterial);

        // Convertir el material actualizado a DTO y devolverlo
        return convertToDTO(updatedMaterial);
    }

    @Override
    @Transactional
    public void deleteMaterials(int materialsId) {
        // Buscar el material por ID
        Materials materials = materialsRepository.findById(materialsId)
                .orElseThrow(() -> new BusinessException("MATERIAL_NOT_FOUND", "Material no encontrado con ID: " + materialsId));

        // Si se encuentra el material, eliminar la imagen si existe.
        // Nota: la eliminación de la imagen es una operación side-effect externa y
        // no participa en la transacción de la BD; si falla la eliminación, se
        // captura y se delega la política (retry/alert) a la estrategia de storage.
        String imagePath = materials.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                imageStorageStrategy.deleteImage(imagePath); // Utilizar la estrategia para eliminar la imagen
            } catch (Exception ex) {
                // No impedimos la eliminación lógica del recurso en BD; registrar/alertar.
                // Aquí preferimos continuar y eliminar el registro en BD para no dejar
                // basura referencial en la aplicación.
            }
        }

        // Eliminar el material de la base de datos
        materialsRepository.deleteById(materialsId);
    }

    @Override
    public List<MaterialsDTO> getAllMaterials() {
        return materialsQueryService.getAllMaterials();
    }

    /**
     * Obtiene todos los materiales con paginación y ordenamiento.
     * 
     * @param pageable Configuración de paginación (página, tamaño, ordenamiento)
     * @return Page de MaterialsDTO con metadatos de paginación
     */
    @Override
    public Page<MaterialsDTO> getAllMaterialsPaginated(Pageable pageable) {
        Page<Materials> materialsPage = materialsRepository.findAll(pageable);
        return materialsPage.map(this::convertToDTO);
    }

    @Override
    public MaterialsDTO getMaterialsById(int materialsId) {
        return materialsQueryService.getById(materialsId);
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
        return materialsQueryService.getAllMaterialsSortedByPrice(ascending);
    }

    // ==================== DOMAIN EVENTS ====================

    /**
     * Verifica si el stock de un material está por debajo del umbral
     * y publica un MaterialLowStockEvent si es necesario.
     * 
     * PROPÓSITO:
     * - Detectar materiales con stock crítico
     * - Notificar al sistema mediante eventos de dominio
     * - Permitir reacciones asíncronas (emails, alertas, compras automáticas)
     * 
     * LÓGICA:
     * - Si stock < LOW_STOCK_THRESHOLD (10 unidades por defecto)
     * - Publicar evento MaterialLowStockEvent
     * - Los listeners (MaterialEventListener) reaccionarán de forma asíncrona
     * 
     * CASOS DE USO:
     * 1. Después de crear un material con stock inicial bajo
     * 2. Después de actualizar un material y reducir su stock
     * 3. Después de un préstamo que reduce el stock disponible
     * 
     * @param material El material a verificar
     */
    private void checkAndPublishLowStockEvent(Materials material) {
        if (material.getStock() < LOW_STOCK_THRESHOLD) {
            MaterialLowStockEvent event = new MaterialLowStockEvent(
                material,
                LOW_STOCK_THRESHOLD
            );
            eventPublisher.publishEvent(event);
        }
    }

}
