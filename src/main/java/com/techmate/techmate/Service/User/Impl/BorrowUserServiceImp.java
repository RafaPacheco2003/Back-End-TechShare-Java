package com.techmate.techmate.Service.User.Impl;

import java.util.stream.Collectors;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.techmate.techmate.Entity.Status; // Importa tu propia clase Status

import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.DTO.DetailsBorrowDTO;
import com.techmate.techmate.Entity.Borrow;
import com.techmate.techmate.Entity.DetailsBorrow;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.RoleMaterials;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.BorrowRepository;
import com.techmate.techmate.Repository.DetailsBorrowRepository;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.RoleMaterialsRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Security.TokenUtils;
import com.techmate.techmate.Service.User.BorrowUserService;

@Service
public class BorrowUserServiceImp implements BorrowUserService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private DetailsBorrowRepository detailsBorrowRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleMaterialsRepository roleMaterialsRepository;

    private Borrow convertToEntity(BorrowDTO borrowDTO) {
        Borrow borrow = new Borrow();
        borrow.setBorrowId(borrowDTO.getBorrowId());
        borrow.setDate(borrowDTO.getDate());
        borrow.setStatus(Status.PROCCES);
        borrow.setAmount(borrowDTO.getAmount());
        borrow.setDetails(borrowDTO.getDetails().stream()
                .map(detailDTO -> convertDetailsBorrowToEntity(detailDTO, borrow))
                .collect(Collectors.toList()));

        Usuario user = usuarioRepository.findById(borrowDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + borrowDTO.getUsuarioId()));
        borrow.setUsuario(user);

        return borrow;
    }

    private BorrowDTO convertToDTO(Borrow borrow) {
        BorrowDTO dto = new BorrowDTO();
        dto.setBorrowId(borrow.getBorrowId());
        dto.setDate(borrow.getDate());
        dto.setStatus(borrow.getStatus());
        dto.setAmount(borrow.getAmount());

        dto.setDetails(borrow.getDetails().stream()
                .map(this::convertDetailsBorrowToDTO)
                .collect(Collectors.toList()));

        dto.setUsuarioId(borrow.getUsuario().getId());
        // Obtener el nombre de usuario
        dto.setUsuarioName(borrow.getUsuario().getUser_name()); // Asume que el campo se llama 'nombre'

        return dto;
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
    public BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception {
        // Verificar que el usuario tenga roles
        if (roles == null || roles.isEmpty()) {
            throw new Exception("El usuario no tiene roles asignados. No se puede crear el préstamo.");
        }

        // Convertir BorrowDTO en la entidad Borrow
        Borrow borrow = convertToEntity(borrowDTO);
        borrow.setAmount(0);

        // Persistir borrow para generar el borrow_id
        borrow = borrowRepository.save(borrow);

        double totalAmount = 0;

        for (DetailsBorrowDTO detailDTO : borrowDTO.getDetails()) {
            Materials material = materialsRepository.findById(detailDTO.getMaterialsId())
                    .orElseThrow(() -> new Exception("Material no encontrado con ID: " + detailDTO.getMaterialsId()));

            // Verificar stock suficiente (sin modificarlo)
            if (material.getStock() < detailDTO.getQuantity()) {
                throw new Exception("Stock insuficiente para el material con ID: " + material.getMaterialsId());
            }

            // Obtener los roles permitidos para este material desde RoleMaterials
            List<RoleMaterials> roleMaterialsList = roleMaterialsRepository.findByMaterials(material);

            // Obtener los IDs de roles permitidos para este material
            List<Integer> rolesPermitidos = roleMaterialsList.stream()
                    .map(roleMaterials -> roleMaterials.getRole().getRoleId())
                    .collect(Collectors.toList());

            // Verificar si al menos uno de los roles del usuario está permitido para este
            // material
            boolean tieneRolPermitido = roles.stream().anyMatch(rolesPermitidos::contains);
            if (!tieneRolPermitido) {
                // Si no tiene permisos, lanza una excepción con detalles del material
                throw new Exception(
                        "El usuario no tiene permisos para acceder al material: " + material.getName() +
                                " (ID: " + material.getMaterialsId() + ")");
            }

            // Convertir y guardar los detalles (sin modificar el stock)
            DetailsBorrow detailsBorrow = convertDetailsBorrowToEntity(detailDTO, borrow);
            totalAmount += detailsBorrow.getTotalPrice();

            // Guardar los detalles del préstamo sin afectar el stock del material
            detailsBorrowRepository.save(detailsBorrow);
        }

        // Actualizar el monto total del préstamo
        borrow.setAmount(totalAmount);
        borrow = borrowRepository.save(borrow);

        // Retornar el DTO del préstamo creado
        return convertToDTO(borrow);
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

    @Override
    public List<BorrowDTO> getAllBorrowsByUserId(Integer userId) {
        List<Borrow> borrows = borrowRepository.findByUsuarioId(userId); // Asumiendo que tienes este método en tu
                                                                         // repositorio
        return borrows.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        return TokenUtils.getRolesFromToken(token);
    }
}
