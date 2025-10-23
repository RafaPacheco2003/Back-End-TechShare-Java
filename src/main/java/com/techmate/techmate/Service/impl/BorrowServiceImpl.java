package com.techmate.techmate.Service.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.techmate.techmate.Service.borrow.manager.IBorrowStockManager;
import com.techmate.techmate.dto.BorrowDTO;
import com.techmate.techmate.dto.DetailsBorrowDTO;
import com.techmate.techmate.entity.Borrow;
import com.techmate.techmate.entity.DetailsBorrow;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Status;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.event.BorrowCreatedEvent;
import com.techmate.techmate.event.BorrowReturnedEvent;
import com.techmate.techmate.exception.BorrowBusinessException;
import com.techmate.techmate.exception.BusinessException;
import com.techmate.techmate.repository.BorrowRepository;
import com.techmate.techmate.repository.DetailsBorrowRepository;
import com.techmate.techmate.repository.MaterialsRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.security.TokenUtils;
import com.techmate.techmate.Service.BorrowService;
@Service
public class BorrowServiceImpl implements BorrowService {
    private final BorrowRepository borrowRepository;
    private final MaterialsRepository materialsRepository;
    private final DetailsBorrowRepository detailsBorrowRepository;
    private final UsuarioRepository usuarioRepository;
    private final IBorrowStockManager borrowStockManager;
    private final ApplicationEventPublisher eventPublisher;

    public BorrowServiceImpl(BorrowRepository borrowRepository, MaterialsRepository materialsRepository,
            DetailsBorrowRepository detailsBorrowRepository, UsuarioRepository usuarioRepository,
            IBorrowStockManager borrowStockManager,
            ApplicationEventPublisher eventPublisher) {
        this.borrowRepository = borrowRepository;
        this.materialsRepository = materialsRepository;
        this.detailsBorrowRepository = detailsBorrowRepository;
        this.usuarioRepository = usuarioRepository;
        this.borrowStockManager = borrowStockManager;
        this.eventPublisher = eventPublisher;
    }

    // ==================== FALLBACK HELPERS (compatibilidad con tests) ====================
    /**
     * Valida disponibilidad de stock usando IBorrowStockManager si existe;
     * en caso contrario aplica la lógica directa con el repositorio (fallback para tests).
     */
    private void validateStockAvailabilityOrFallback(Integer materialId, int requestedQuantity) {
        if (borrowStockManager != null) {
            borrowStockManager.validateStockAvailability(materialId, requestedQuantity);
            return;
        }

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));
        if (material.getBorrowable_stock() < requestedQuantity) {
            throw BorrowBusinessException.insufficientStock(materialId, requestedQuantity, material.getBorrowable_stock());
        }
    }

    private void reduceStockOrFallback(Integer materialId, int quantity) {
        if (borrowStockManager != null) {
            borrowStockManager.reduceStock(materialId, quantity);
            return;
        }

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));

        if (material.getBorrowable_stock() < quantity) {
            throw BorrowBusinessException.insufficientStock(materialId, quantity, material.getBorrowable_stock());
        }

        material.setBorrowable_stock(material.getBorrowable_stock() - quantity);
        materialsRepository.save(material);
    }

    private void restoreStockOrFallback(Integer materialId, int quantity) {
        if (borrowStockManager != null) {
            borrowStockManager.restoreStock(materialId, quantity);
            return;
        }

        Materials material = materialsRepository.findById(materialId)
                .orElseThrow(() -> BorrowBusinessException.materialNotFound(materialId));
        material.setBorrowable_stock(material.getBorrowable_stock() + quantity);
        materialsRepository.save(material);
    }

    private BorrowDTO convertToDTO(Borrow borrow) {
        BorrowDTO dto = new BorrowDTO();
        dto.setBorrowId(borrow.getBorrowId());
        dto.setDate(borrow.getDate());
        dto.setStatus(borrow.getStatus());
        dto.setAmount(borrow.getAmount());
        dto.setStartDate(borrow.getStartDate());
        dto.setEndDate(borrow.getEndDate());
        dto.setReturnDate(borrow.getReturnDate());;
    
        // Asegúrate de que el usuario y admin se asignen correctamente
        if (borrow.getUsuario() != null) {
            dto.setUsuarioId(borrow.getUsuario().getId());
            dto.setUsuarioName(borrow.getUsuario().getUser_name());
        }
    
        if (borrow.getAdmin() != null) {
            dto.setAdminId(borrow.getAdmin().getId());
            dto.setAdminName(borrow.getAdmin().getUser_name());
        }
    
        // Mapear los detalles del préstamo
        dto.setDetails(borrow.getDetails().stream()
                .map(this::convertDetailsBorrowToDTO)
                .collect(Collectors.toList()));
    
        return dto;
    }
    
    private Borrow convertToEntity(BorrowDTO borrowDTO) {
        Borrow borrow = new Borrow();
        borrow.setBorrowId(borrowDTO.getBorrowId());
        borrow.setDate(borrowDTO.getDate());
    borrow.setStatus(Status.PENDING);  // Asignar un estado inicial adecuado
        borrow.setAmount(borrowDTO.getAmount());
    
        // Asignación de detalles
        borrow.setDetails(borrowDTO.getDetails().stream()
                .map(detailDTO -> convertDetailsBorrowToEntity(detailDTO, borrow))
                .collect(Collectors.toList()));
    
        // Obtener el usuario adminId
    Usuario admin = usuarioRepository.findById(borrowDTO.getAdminId())
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Usuario no encontrado con ID: " + borrowDTO.getAdminId()));
        borrow.setAdmin(admin);  // Asignamos el admin
    
        // Asignar el usuario (en este caso adminId también puede referirse a un usuario)
    Usuario usuario = usuarioRepository.findById(borrowDTO.getUsuarioId())
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Usuario no encontrado con ID: " + borrowDTO.getUsuarioId()));
        borrow.setUsuario(usuario);  // Asignamos el usuario
    
        return borrow;
    }
    

    private DetailsBorrow convertDetailsBorrowToEntity(DetailsBorrowDTO detailDTO, Borrow borrow) {
        // Crear una nueva instancia de DetailsBorrow
        DetailsBorrow detailsBorrow = new DetailsBorrow();

        detailsBorrow.setBorrow(borrow);
        detailsBorrow.setQuantity(detailDTO.getQuantity());

        // Obtener el material asociado
    Materials material = materialsRepository.findById(detailDTO.getMaterialsId())
        .orElseThrow(
            () -> BorrowBusinessException.materialNotFound(detailDTO.getMaterialsId()));
        detailsBorrow.setMaterials(material); // Establecer la relación con Materials

        // Asignar el precio unitario desde el material
        detailsBorrow.setUnitPrice(material.getPrice()); // Establecer el precio unitario
        detailsBorrow.setTotalPrice(material.getPrice() * detailDTO.getQuantity()); // Calcular el precio total

        return detailsBorrow;
    }

    private DetailsBorrowDTO convertDetailsBorrowToDTO(DetailsBorrow detailsBorrow) {
        DetailsBorrowDTO dto = new DetailsBorrowDTO();
        dto.setDetailsBorrowId(detailsBorrow.getDetailsBorrowId());

        if (detailsBorrow.getMaterials() != null) {
            dto.setMaterialsId(detailsBorrow.getMaterials().getMaterialsId());
        } else {
            dto.setMaterialsId(null); // O lanzar una excepción si es necesario
        }

        // Establecer el borrowId en el DTO
        if (detailsBorrow.getBorrow() != null) {
            dto.setBorrowId(detailsBorrow.getBorrow().getBorrowId()); // Aquí estableces el borrowId en el DTO
        }

        dto.setQuantity(detailsBorrow.getQuantity());
        dto.setUnitPrice(detailsBorrow.getUnitPrice());
        dto.setTotalPrice(detailsBorrow.getTotalPrice());

        return dto;
    }

    @Override
    @Transactional
    public void updateBorrowStatus(Integer borrowId, Status newStatus, Integer adminId) throws Exception {
        // Buscar el préstamo por ID
    Borrow borrow = borrowRepository.findById(borrowId)
        .orElseThrow(() -> new BusinessException("BORROW_NOT_FOUND", "Préstamo no encontrado con ID: " + borrowId));
    
        // Verificar el estado actual del préstamo
        if (borrow.getStatus() != Status.PENDING && borrow.getStatus() != Status.LOANED) {
            throw new Exception("Solo se puede modificar el estado de un préstamo en estado PENDING o LOANED");
        }
    
        // Establecer el adminId en el préstamo
    Usuario admin = usuarioRepository.findById(adminId)
        .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Administrador no encontrado con ID: " + adminId));
        borrow.setAdmin(admin); // Asignar el admin al préstamo
    
        switch (newStatus) {
            case REJECTED:
                borrow.setStatus(Status.REJECTED);
                borrow.setEndDate(new Date());
                break;

            case LOANED:
                if (borrow.getStatus() == Status.PENDING) {
                    for (DetailsBorrow detail : borrow.getDetails()) {
                        Integer materialId = detail.getMaterials().getMaterialsId();
                        int qty = detail.getQuantity();
                        // Validar y reducir stock usando el manager especializado o fallback
                        validateStockAvailabilityOrFallback(materialId, qty);
                        reduceStockOrFallback(materialId, qty);
                    }
                    borrow.setStartDate(new Date()); // Actualizar la fecha de inicio
                    borrow.setStatus(Status.LOANED);
                    
                    // Publicar evento de préstamo creado
                    publishBorrowCreatedEvent(borrow);
                }
                break;
    
            case RETURNED:
                if (borrow.getStatus() != Status.LOANED) {
                    throw new Exception("El préstamo debe estar en estado LOANED para ser devuelto");
                }
                for (DetailsBorrow detail : borrow.getDetails()) {
                    Integer materialId = detail.getMaterials().getMaterialsId();
                    int qty = detail.getQuantity();
                    restoreStockOrFallback(materialId, qty);
                }
                borrow.setStatus(Status.RETURNED);
                borrow.setReturnDate(new Date());
                borrow.setEndDate(new Date());
                
                // Publicar evento de devolución
                publishBorrowReturnedEvent(borrow);
                break;
    
            default:
                throw new BusinessException("INVALID_BORROW_STATUS", "Estado no válido para modificar el préstamo");
        }

        borrowRepository.save(borrow); // Guardar el préstamo actualizado
    }
    
    @Override
    public List<BorrowDTO> getAllBorrowDTO() {
        return borrowRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowDTO> getBorrowByStatus(String status) {
        Status statusBorrow;

        switch (status.toUpperCase()) {
            case "PROCESS":
                statusBorrow = Status.PENDING;
                break;
            case "REJECTED":
                statusBorrow = Status.REJECTED;
                break;
            case "LOANED":
                statusBorrow = Status.LOANED;
                break;
            case "RETURNED":
                statusBorrow = Status.RETURNED;
                break;
            default:
                throw new IllegalArgumentException("Tipo de estado inválido: " + status);
        }

        // Cambiar a List<Borrow> y luego mapear a List<BorrowDTO>
        return borrowRepository.findByStatus(statusBorrow).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowDTO> getBorrowByDate(Date startDate, Date endDate) {
        //
        return borrowRepository.findByDateBetween(startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

    // ==================== DOMAIN EVENTS ====================

    /**
     * Publica un evento cuando se crea un préstamo (transición PROCESS → BORROWED).
     * 
     * PROPÓSITO:
     * - Notificar al sistema que se ha aprobado y entregado un préstamo
     * - Permitir reacciones asíncronas (emails de confirmación, actualización de estadísticas, etc.)
     * 
     * INFORMACIÓN DEL EVENTO:
     * - ID del préstamo
     * - ID del usuario que solicitó
     * - Fechas del préstamo (fecha, inicio, fin)
     * - Monto total del préstamo
     * 
     * @param borrow El préstamo recién creado/aprobado
     */
    private void publishBorrowCreatedEvent(Borrow borrow) {
        BorrowCreatedEvent event = new BorrowCreatedEvent(borrow);
        eventPublisher.publishEvent(event);
    }

    /**
     * Publica un evento cuando se devuelve un préstamo (transición BORROWED → RETURNED).
     * 
     * PROPÓSITO:
     * - Notificar al sistema que se ha completado una devolución
     * - Detectar devoluciones tardías para aplicar penalizaciones
     * - Permitir reacciones asíncronas (emails, cálculo de multas, actualización de historial)
     * 
     * LÓGICA:
     * - El evento detecta automáticamente si la devolución fue tardía
     * - Compara returnDate con endDate para marcar wasLate=true si corresponde
     * - Los listeners pueden implementar lógica de penalización
     * 
     * @param borrow El préstamo que se devolvió
     */
    private void publishBorrowReturnedEvent(Borrow borrow) {
        Date returnDate = borrow.getReturnDate() != null ? borrow.getReturnDate() : new Date();
        BorrowReturnedEvent event = new BorrowReturnedEvent(borrow, returnDate);
        eventPublisher.publishEvent(event);
    }

}
