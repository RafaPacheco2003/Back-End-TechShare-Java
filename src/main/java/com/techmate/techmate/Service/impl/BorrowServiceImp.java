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
import com.techmate.techmate.Entity.Status;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.BorrowRepository;
import com.techmate.techmate.Repository.DetailsBorrowRepository;
import com.techmate.techmate.Repository.MaterialsRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Service.BorrowService;

@Service
public class BorrowServiceImp implements BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private MaterialsRepository materialsRepository;

    @Autowired
    private DetailsBorrowRepository detailsBorrowRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    public BorrowDTO createBorrowDTO(BorrowDTO borrowDTO) throws Exception {
        // Primero convierte el DTO de préstamo (BorrowDTO) en una entidad (Borrow) sin
        // detalles todavía
        Borrow borrow = convertToEntity(borrowDTO);

        // Inicializa el monto total del préstamo en 0 ya que aún no se han agregado los
        // detalles
        borrow.setAmount(0);

        // Guarda el objeto Borrow en la base de datos, generando así el borrow_id (es
        // necesario persistir primero el Borrow
        // para poder relacionar los detalles luego)
        borrow = borrowRepository.save(borrow);

        // Inicializa una variable para calcular el monto total del préstamo (sumará el
        // total de los detalles)
        double totalAmount = 0;

        // Iterar sobre la lista de detalles (DetailsBorrowDTO) contenida en el
        // BorrowDTO
        for (DetailsBorrowDTO detailDTO : borrowDTO.getDetails()) {
            // Buscar el material asociado a este detalle mediante el ID del material
            Materials material = materialsRepository.findById(detailDTO.getMaterialsId())
                    .orElseThrow(() -> new Exception("Material no encontrado con ID: " + detailDTO.getMaterialsId()));

            // Verificar si el stock del material es suficiente para la cantidad que se
            // quiere prestar
            if (material.getStock() < detailDTO.getQuantity()) {
                throw new Exception("Stock insuficiente para el material con ID: " + material.getMaterialsId());
            }

            // Convertir el DetailsBorrowDTO en la entidad DetailsBorrow, asociándola al
            // borrow ya creado
            DetailsBorrow detailsBorrow = convertDetailsBorrowToEntity(detailDTO, borrow);

            // Sumar el precio total de este detalle (cantidad x precio unitario) al monto
            // total del borrow
            totalAmount += detailsBorrow.getTotalPrice();

            // Actualizar el stock del material restando la cantidad solicitada en este
            // detalle
            // ----material.setStock(material.getStock() - detailDTO.getQuantity());

            // Guardar el material actualizado con el nuevo stock en la base de datos
            materialsRepository.save(material);

            // Guardar el detalle del préstamo en la base de datos, ahora asociado al borrow
            // ya persistido
            detailsBorrowRepository.save(detailsBorrow);
        }

        // Una vez procesados todos los detalles, se actualiza el monto total del
        // préstamo con la suma de todos los detalles
        borrow.setAmount(totalAmount);

        // Guardar el préstamo actualizado (con el monto total) en la base de datos
        borrow = borrowRepository.save(borrow);

        // Convertir el préstamo actualizado (Borrow) en un DTO (BorrowDTO) y devolverlo
        return convertToDTO(borrow);
    }

    @Override
    @Transactional
    public void updateBorrowStatus(Integer borrowId, Status newStatus) throws Exception {
        // Buscar el préstamo por ID
        Borrow borrow = borrowRepository.findById(borrowId)
                .orElseThrow(() -> new Exception("Préstamo no encontrado con ID: " + borrowId));

        // Verificar el estado actual del préstamo
        if (borrow.getStatus() != Status.PROCCES && borrow.getStatus() != Status.BORROWED) {
            throw new Exception("Solo se puede modificar el estado de un préstamo en estado PROCESS o BORROWED");
        }

        switch (newStatus) {
            case REJECETD:
                // Eliminar el préstamo si el estado es REJECTED
                borrowRepository.delete(borrow);
                break;

            case BORROWED:
                // Si el estado actual es PROCESS, procesar el préstamo
                if (borrow.getStatus() == Status.PROCCES) {
                    // Restar el borrowed_stock de los materiales asociados
                    for (DetailsBorrow detail : borrow.getDetails()) {
                        Materials material = detail.getMaterials();
                        if (material.getStock() < detail.getQuantity()) {
                            throw new Exception(
                                    "Stock insuficiente para el material con ID: " + material.getMaterialsId());
                        }
                        material.setBorrowable_stock(material.getBorrowable_stock() - detail.getQuantity()); // Decrementar
                        materialsRepository.save(material); // Guardar el material actualizado
                    }
                }
                // Actualizar el estado del préstamo a BORROWED
                borrow.setStatus(Status.BORROWED);
                borrowRepository.save(borrow); // Guardar el préstamo actualizado
                break;

            case RETURNED:
                // Verificar que el estado actual sea BORROWED antes de actualizar
                if (borrow.getStatus() != Status.BORROWED) {
                    throw new Exception("El préstamo debe estar en estado BORROWED para ser devuelto");
                }

                // Sumar el borrowed_stock de los materiales asociados
                for (DetailsBorrow detail : borrow.getDetails()) {
                    Materials material = detail.getMaterials();
                    material.setBorrowable_stock(material.getBorrowable_stock() + detail.getQuantity()); // Aumentar
                    materialsRepository.save(material); // Guardar el material actualizado
                }
                // Actualizar el estado del préstamo a RETURNED
                borrow.setStatus(Status.RETURNED);
                borrowRepository.save(borrow); // Guardar el préstamo actualizado
                break;

            default:
                throw new Exception("Estado no válido para modificar el préstamo");
        }
    }

}
