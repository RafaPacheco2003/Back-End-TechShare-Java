package com.techmate.techmate.DTO;



import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private Set<Integer> roles; // Suponiendo que los roles son identificadores
}

