package com.techmate.techmate.Service.movements.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.techmate.techmate.dto.MovementResponse;
import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.entity.MoveType;

public class MovementsMapperTest {

    @Test
    void toResponse_mapsAllFields() {
        MovementsDTO dto = new MovementsDTO();
        dto.setMovementsId(42);
    dto.setMoveType(MoveType.IN);
        dto.setQuantity(5);
        Date now = new Date();
        dto.setDate(now);
        dto.setComment("test comment");
        dto.setAdminId(7);
        dto.setAdminName("admin");
        dto.setMaterialsId(99);
        dto.setMaterialsName("material-name");

        MovementsMapper mapper = new MovementsMapper();
        MovementResponse resp = mapper.toResponse(dto);

        assertEquals(dto.getMovementsId(), resp.getMovementsId());
        assertEquals(dto.getMoveType(), resp.getMoveType());
        assertEquals(dto.getQuantity(), resp.getQuantity());
        assertEquals(dto.getDate(), resp.getDate());
        assertEquals(dto.getComment(), resp.getComment());
        assertEquals(dto.getAdminId(), resp.getAdminId());
        assertEquals(dto.getAdminName(), resp.getAdminName());
        assertEquals(dto.getMaterialsId(), resp.getMaterialsId());
        assertEquals(dto.getMaterialsName(), resp.getMaterialsName());
    }
}
