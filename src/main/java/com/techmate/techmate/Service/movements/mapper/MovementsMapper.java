package com.techmate.techmate.service.movements.mapper;

import org.springframework.stereotype.Component;

import com.techmate.techmate.dto.MovementResponse;
import com.techmate.techmate.dto.MovementsDTO;

@Component
public class MovementsMapper {

    public MovementResponse toResponse(MovementsDTO dto) {
        if (dto == null) return null;
        MovementResponse resp = new MovementResponse();
        resp.setMovementsId(dto.getMovementsId());
        resp.setMoveType(dto.getMoveType());
        resp.setQuantity(dto.getQuantity());
        resp.setDate(dto.getDate());
        resp.setComment(dto.getComment());
        resp.setAdminId(dto.getAdminId());
        resp.setAdminName(dto.getAdminName());
        resp.setMaterialsId(dto.getMaterialsId());
        resp.setMaterialsName(dto.getMaterialsName());
        return resp;
    }
}

