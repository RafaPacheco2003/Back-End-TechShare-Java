package com.techmate.techmate.Service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.DTO.DetailsBorrowDTO;
import com.techmate.techmate.Entity.Borrow;
import com.techmate.techmate.Entity.DetailsBorrow;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.Status;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.BorrowRepository;
import com.techmate.techmate.Repository.DetailsBorrowRepository;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Security.TokenUtils;
import com.techmate.techmate.Service.BorrowService;
import com.techmate.techmate.Service.User.BorrowUserService;

@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private DetailsBorrowRepository detailsBorrowRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
        borrow.setStatus(Status.PROCCES);  // Asignar un estado inicial adecuado
        borrow.setAmount(borrowDTO.getAmount());
    
        // Asignación de detalles
        borrow.setDetails(borrowDTO.getDetails().stream()
                .map(detailDTO -> convertDetailsBorrowToEntity(detailDTO, borrow))
                .collect(Collectors.toList()));
    
        // Obtener el usuario adminId
        Usuario admin = usuarioRepository.findById(borrowDTO.getAdminId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + borrowDTO.getAdminId()));
        borrow.setAdmin(admin);  // Asignamos el admin
    
        // Asignar el usuario (en este caso adminId también puede referirse a un usuario)
        Usuario usuario = usuarioRepository.findById(borrowDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + borrowDTO.getUsuarioId()));
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
                        () -> new RuntimeException("Material no encontrado con ID: " + detailDTO.getMaterialsId()));
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
                .orElseThrow(() -> new Exception("Préstamo no encontrado con ID: " + borrowId));
    
        // Verificar el estado actual del préstamo
        if (borrow.getStatus() != Status.PROCCES && borrow.getStatus() != Status.BORROWED) {
            throw new Exception("Solo se puede modificar el estado de un préstamo en estado PROCESS o BORROWED");
        }
    
        // Establecer el adminId en el préstamo
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new Exception("Administrador no encontrado con ID: " + adminId));
        borrow.setAdmin(admin); // Asignar el admin al préstamo
    
        switch (newStatus) {
            case REJECETD:
                borrow.setStatus(Status.REJECETD);
                borrow.setEndDate(new Date());
                break;
    
            case BORROWED:
                if (borrow.getStatus() == Status.PROCCES) {
                    for (DetailsBorrow detail : borrow.getDetails()) {
                        Materials material = detail.getMaterials();
                        if (material.getBorrowable_stock() < detail.getQuantity()) {
                            throw new RuntimeException(
                                    "Stock insuficiente para el material con ID: " + material.getMaterialsId());
                        }
                        material.setBorrowable_stock(material.getBorrowable_stock() - detail.getQuantity());
                        materialsRepository.save(material);
                    }
                    borrow.setStartDate(new Date()); // Actualizar la fecha de inicio
                    borrow.setStatus(Status.BORROWED);
                }
                break;
    
            case RETURNED:
                if (borrow.getStatus() != Status.BORROWED) {
                    throw new Exception("El préstamo debe estar en estado BORROWED para ser devuelto");
                }
                for (DetailsBorrow detail : borrow.getDetails()) {
                    Materials material = detail.getMaterials();
                    material.setBorrowable_stock(material.getBorrowable_stock() + detail.getQuantity());
                    materialsRepository.save(material);
                }
                borrow.setStatus(Status.RETURNED);
                borrow.setReturnDate(new Date());
                borrow.setEndDate(new Date());
                break;
    
            default:
                throw new Exception("Estado no válido para modificar el préstamo");
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
            case "PROCCES":
                statusBorrow = Status.PROCCES;
                break;
            case "REJECETD":
                statusBorrow = Status.REJECETD;
                break;
            case "BORROWED":
                statusBorrow = Status.BORROWED;
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

}
