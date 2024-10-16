package com.techmate.techmate.DTO;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    private String user_name; // Cambiado de 'nombre' a 'user_name'
    private String first_name; // Nuevo campo
    private String last_name; // Nuevo campo
    private String email;
    private String password;
    private Set<Integer> roles; // Suponiendo que los roles son identificadores
}
