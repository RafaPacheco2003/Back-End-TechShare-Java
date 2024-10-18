package com.techmate.techmate.Service.impl;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techmate.techmate.DTO.BorrowDTO;
import com.techmate.techmate.DTO.DetailsBorrowDTO;
import com.techmate.techmate.Entity.Borrow;
import com.techmate.techmate.Entity.DetailsBorrow;
import com.techmate.techmate.Entity.Materials;
import com.techmate.techmate.Repository.BorrowRepository;
import com.techmate.techmate.Repository.DetailsBorrowRepository;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Service.BorrowService;

@Service
public class BorrowServiceImp implements BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private DetailsBorrowRepository detailsBorrowRepository;

    private Borrow convertToEntity(BorrowDTO borrowDTO) {
        Borrow borrow = new Borrow();
        borrow.setDate(borrowDTO.getDate());
        borrow.setStatus(borrowDTO.getStatus());
        borrow.setAmount(borrowDTO.getAmount());
        borrow.setDetails(borrowDTO.getDetails().stream()
                .map(detailDTO -> convertDetailsBorrowToEntity(detailDTO, borrow))
                .collect(Collectors.toList()));
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

        return dto;
    }

    private DetailsBorrow convertDetailsBorrowToEntity(DetailsBorrowDTO detailDTO, Borrow borrow) {
        // Crear una nueva instancia de DetailsBorrow
        DetailsBorrow detailsBorrow = new DetailsBorrow();
        detailsBorrow.setBorrow(borrow);
        detailsBorrow.setQuantity(detailDTO.getQuantity());
        detailsBorrow.setUnitPrice(detailDTO.getUnitPrice());
        detailsBorrow.setTotalPrice(detailDTO.getUnitPrice() * detailDTO.getQuantity());
        
        // Obtener el material asociado
        Materials material = materialsRepository.findById(detailDTO.getMaterialsId())
                .orElseThrow(() -> new RuntimeException("Material no encontrado con ID: " + detailDTO.getMaterialsId()));
        detailsBorrow.setMaterials(material); // Establecer la relación con Materials
        
        return detailsBorrow;
        
    }

    private DetailsBorrowDTO convertDetailsBorrowToDTO(DetailsBorrow detailsBorrow) {
        DetailsBorrowDTO dto = new DetailsBorrowDTO();
        
        if (detailsBorrow.getMaterials() != null) {
            dto.setMaterialsId(detailsBorrow.getMaterials().getMaterialsId());
        } else {
            dto.setMaterialsId(null); // O lanzar una excepción si es necesario
        }

        dto.setQuantity(detailsBorrow.getQuantity());
        dto.setUnitPrice(detailsBorrow.getUnitPrice());
        dto.setTotalPrice(detailsBorrow.getTotalPrice());
        return dto;
    }

    @Override
    @Transactional
    public BorrowDTO createBorrowDTO(BorrowDTO borrowDTO) throws Exception {
        Borrow borrow = convertToEntity(borrowDTO);
        double totalAmount = 0;

        // Iterar sobre los detalles del préstamo
        for (DetailsBorrowDTO detailDTO : borrowDTO.getDetails()) {
            Materials material = materialsRepository.findById(detailDTO.getMaterialsId())
                    .orElseThrow(() -> new Exception("Material no encontrado con ID: " + detailDTO.getMaterialsId()));

            // Verificar si hay suficiente stock
            if (material.getStock() < detailDTO.getQuantity()) {
                throw new Exception("Stock insuficiente para el material con ID: " + material.getMaterialsId());
            }

            // Crear el detalle del préstamo
            DetailsBorrow detailsBorrow = convertDetailsBorrowToEntity(detailDTO, borrow); // Uso del método modificado
            totalAmount += detailsBorrow.getTotalPrice();

            // Actualizar el stock del material
            material.setStock(material.getStock() - detailDTO.getQuantity());
            materialsRepository.save(material); // Guardar el nuevo stock

            // Guardar el detalle del préstamo
            detailsBorrowRepository.save(detailsBorrow);
        }

        // Actualizar el monto total del borrow
        borrow.setAmount(totalAmount);
        borrow = borrowRepository.save(borrow); // Guardar el borrow con el monto actualizado

        return convertToDTO(borrow); // Devolver el DTO actualizado
    }
}
