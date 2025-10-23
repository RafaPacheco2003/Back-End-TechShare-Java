package com.techmate.techmate.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmate.techmate.Service.MovementsService;
import com.techmate.techmate.Service.movements.mapper.MovementsMapper;
import com.techmate.techmate.dto.MovementsDTO;
import com.techmate.techmate.dto.MovementResponse;
import com.techmate.techmate.testutils.TestAuthUtils;
import com.techmate.techmate.testutils.TestAuthConfig;
import com.techmate.techmate.entity.MoveType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@org.springframework.context.annotation.Import(TestAuthConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovementsControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovementsService movementsService;

    @MockitoBean
    private MovementsMapper movementsMapper;

    @Autowired
    private TestAuthUtils authUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void securedGetMovementById_withValidToken_returnsOk() throws Exception {
        MovementsDTO dto = new MovementsDTO();
        dto.setMovementsId(42);
        dto.setQuantity(7);

    MovementResponse resp = new MovementResponse(42, MoveType.STOCK_ADD, 7, new java.util.Date(), "", 1, "Admin", 2, "Mat");

        when(movementsService.getMovementsByID(42)).thenReturn(dto);
        when(movementsMapper.toResponse(eq(dto))).thenReturn(resp);


    // generate a real token signed by TokenUtils with ADMIN role (required by /admin/**)
    String token = authUtils.createTokenWithRoles(1, "test@example.com", "testuser", "ADMIN");

        mockMvc.perform(get("/admin/movement/42")
                .param("id", "42")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movements_id").value(42))
                .andExpect(jsonPath("$.quantity").value(7));
    }

    @Test
    void securedCreateMovement_withValidToken_returnsCreated() throws Exception {
        MovementsDTO created = new MovementsDTO();
        created.setMovementsId(99);
        created.setQuantity(2);
    created.setMoveType(MoveType.LOAN);
        created.setMaterialsId(5);

    MovementResponse resp = new MovementResponse(99, MoveType.LOAN, 2, new java.util.Date(), "ok", 7, "Admin", 5, "Mat5");

        // generate token and configure movementsService mock to accept it
        String token = authUtils.createTokenWithRoles(7, "u@example.com", "u7", "ADMIN");

        when(movementsService.getUserIdFromToken(token)).thenReturn(7);
        when(movementsService.createMovementsDTO(any(), any())).thenReturn(created);
        when(movementsMapper.toResponse(any())).thenReturn(resp);

        mockMvc.perform(post("/admin/movement/create")
                .header("Authorization", "Bearer " + token)
                .param("quantity", "2")
                .param("moveType", "OUT")
                .param("id_material", "5")
                .param("comment", "ok")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.movements_id").value(99))
                .andExpect(jsonPath("$.move_type").value("OUT"));
    }
}
