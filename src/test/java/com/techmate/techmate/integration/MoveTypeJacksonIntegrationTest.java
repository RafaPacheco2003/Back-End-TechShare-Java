package com.techmate.techmate.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmate.techmate.entity.MoveType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MoveTypeJacksonIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    public static class Payload {
        private MoveType moveType;
        private Integer materialsId;

        public MoveType getMoveType() { return moveType; }
        public void setMoveType(MoveType moveType) { this.moveType = moveType; }
        public Integer getMaterialsId() { return materialsId; }
        public void setMaterialsId(Integer materialsId) { this.materialsId = materialsId; }
    }

    @Test
    public void jacksonSerializesToSnakeCaseAndMoveTypeLegacyToken() throws Exception {
        Payload p = new Payload();
        p.setMoveType(MoveType.BORROW);
        p.setMaterialsId(2);

        String json = objectMapper.writeValueAsString(p);

        // Debe usar snake_case y emitir el token legacy OUT para LOAN
        assertTrue(json.contains("\"move_type\""), "Debe serializar campo como move_type");
        assertTrue(json.contains("\"move_type\":\"OUT\""), "MoveType LOAN debe serializarse como 'OUT'");
        assertTrue(json.contains("\"materials_id\":2"), "materialsId debe serializarse como materials_id");

        // Deserialización desde la API legacy
        String input = "{\"move_type\":\"OUT\",\"materials_id\":2}";
        Payload des = objectMapper.readValue(input, Payload.class);
        assertEquals(MoveType.BORROW, des.getMoveType());
        assertEquals(2, des.getMaterialsId());
    }
}
