// BorrowUserServiceImp.java
package com.techmate.techmate.Service.User.Impl;

import java.util.stream.Collectors;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.DTO.DetailsBorrowDTO;
import com.techmate.techmate.Entity.Borrow;
import com.techmate.techmate.Entity.DetailsBorrow;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Entity.RoleMaterials;
import com.techmate.techmate.Entity.Status;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.*;
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
        dto.setUsuarioName(borrow.getUsuario().getUser_name());

        return dto;
    }

    private DetailsBorrow convertDetailsBorrowToEntity(DetailsBorrowDTO detailDTO, Borrow borrow) {
        DetailsBorrow detailsBorrow = new DetailsBorrow();
        detailsBorrow.setBorrow(borrow);
        detailsBorrow.setQuantity(detailDTO.getQuantity());

        Materials material = materialsRepository.findById(detailDTO.getMaterialsId())
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + detailDTO.getMaterialsId()));
        detailsBorrow.setMaterials(material);
        detailsBorrow.setUnitPrice(material.getPrice());
        detailsBorrow.setTotalPrice(material.getPrice() * detailDTO.getQuantity());

        return detailsBorrow;
    }

    private DetailsBorrowDTO convertDetailsBorrowToDTO(DetailsBorrow detailsBorrow) {
        DetailsBorrowDTO dto = new DetailsBorrowDTO();
        dto.setDetailsBorrowId(detailsBorrow.getDetailsBorrowId());
        dto.setMaterialsId(detailsBorrow.getMaterials().getMaterialsId());
        dto.setBorrowId(detailsBorrow.getBorrow().getBorrowId());
        dto.setQuantity(detailsBorrow.getQuantity());
        dto.setUnitPrice(detailsBorrow.getUnitPrice());
        dto.setTotalPrice(detailsBorrow.getTotalPrice());

        return dto;
    }

    @Override
    @Transactional
    public BorrowDTO createBorrowDTO(BorrowDTO borrowDTO, List<Integer> roles) throws Exception {
        if (roles == null || roles.isEmpty()) {
            throw new Exception("El usuario no tiene roles asignados. No se puede crear el préstamo.");
        }

        Borrow borrow = convertToEntity(borrowDTO);
        borrow.setAmount(0);

        borrow = borrowRepository.save(borrow);
        double totalAmount = 0;

        for (DetailsBorrowDTO detailDTO : borrowDTO.getDetails()) {
            Materials material = materialsRepository.findById(detailDTO.getMaterialsId())
                    .orElseThrow(() -> new Exception("Material no encontrado con ID: " + detailDTO.getMaterialsId()));

            if (material.getStock() < detailDTO.getQuantity()) {
                throw new Exception("Stock insuficiente para el material con ID: " + material.getMaterialsId());
            }

            List<RoleMaterials> roleMaterialsList = roleMaterialsRepository.findByMaterials(material);
            List<Integer> rolesPermitidos = roleMaterialsList.stream()
                    .map(roleMaterials -> roleMaterials.getRole().getRoleId())
                    .collect(Collectors.toList());

            boolean tieneRolPermitido = roles.stream().anyMatch(rolesPermitidos::contains);
            if (!tieneRolPermitido) {
                throw new Exception(
                        "El usuario no tiene permisos para acceder al material: " + material.getName() +
                                " (ID: " + material.getMaterialsId() + ")");
            }

            DetailsBorrow detailsBorrow = convertDetailsBorrowToEntity(detailDTO, borrow);
            totalAmount += detailsBorrow.getTotalPrice();

            detailsBorrowRepository.save(detailsBorrow);
        }

        borrow.setAmount(totalAmount);
        borrow = borrowRepository.save(borrow);

        return convertToDTO(borrow);
    }

    @Override
    public Integer getUserIdFromToken(String token) {
        return TokenUtils.getUserIdFromToken(token);
    }

    @Override
    public List<BorrowDTO> getAllBorrowsByUserId(Integer userId) {
        List<Borrow> borrows = borrowRepository.findByUsuarioId(userId);
        return borrows.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<List<Integer>> getRolesFromToken(String token) {
        return TokenUtils.getRolesFromToken(token);
    }
}
