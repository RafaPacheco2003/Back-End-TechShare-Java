package com.techmate.techmate.dto;

import java.util.Set;
import lombok.Data;

@Data
public class UsuarioDTO {

    private Integer id;
    private String userName; // Correspondiente a 'user_name' en la entidad
    private String firstName; // Correspondiente a 'first_name' en la entidad
    private String lastName;  // Correspondiente a 'last_name' en la entidad
    private String email;
    private Set<String> roles; // Convertido a un Set de Strings para simplificar los roles

}
