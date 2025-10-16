package com.techmate.techmate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserDTO {
    private Integer id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> roles;
}
