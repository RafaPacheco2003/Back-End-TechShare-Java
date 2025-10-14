package com.techmate.techmate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import com.techmate.techmate.entity.Usuario.Gender;
import java.time.LocalDate;
import java.util.Set;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores and hyphens")
    private String user_name;
    
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$", message = "First name contains invalid characters")
    private String first_name;
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s'-]+$", message = "Last name contains invalid characters")
    private String last_name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format", regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    @Size(max = 255, message = "Email must be less than 255 characters")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&_\\-#.,:;])[A-Za-z\\d@$!%*?&_\\-#.,:;]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character (@$!%*?&_-#.,:;)"
    )
    private String password;
    
    // Fecha de nacimiento (opcional)
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    // Género (opcional)
    private Gender gender;
    
    // Roles es opcional - si no se envía, se asignará el rol por defecto
    private Set<@Min(value = 1, message = "Invalid role ID") Integer> roles;
}
