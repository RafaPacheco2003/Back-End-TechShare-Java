package com.techmate.techmate.Service.movements.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.entity.Materials;
import com.techmate.techmate.entity.Movements;
import com.techmate.techmate.entity.Usuario;

@Component
public class MovementMapper {

    public Movements toEntity(MovementsDTO dto, Usuario usuario, Materials materials) {
        if (dto == null) return null;
        Movements m = new Movements();
        m.setMovementsId(dto.getMovementsId());
        m.setMoveType(dto.getMoveType());
        m.setQuantity(dto.getQuantity());
        m.setDate(dto.getDate());
        m.setComment(dto.getComment());
        m.setUsuario(usuario);
        m.setMaterials(materials);
        return m;
    }

    public MovementsDTO toDTO(Movements m, String adminName, String materialName) {
        if (m == null) return null;
        MovementsDTO dto = new MovementsDTO();
        dto.setMovementsId(m.getMovementsId());
        dto.setMoveType(m.getMoveType());
        dto.setQuantity(m.getQuantity());
        dto.setDate(m.getDate());
        dto.setComment(m.getComment());
        dto.setAdminId(m.getUsuario().getId());
        dto.setMaterialsId(m.getMaterials().getMaterialsId());
        dto.setAdminName(adminName);
        dto.setMaterialsName(materialName);
        return dto;
    }
}
