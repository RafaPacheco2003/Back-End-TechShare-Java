package com.techmate.techmate.Security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.DTO.RegisterRequest;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Repository.UsuarioRepository;

@RestController
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.findOneByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        // Crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registerRequest.getNombre());
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario registrado con éxito");
    }
}
