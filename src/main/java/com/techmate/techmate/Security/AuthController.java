package com.techmate.techmate.Security;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.techmate.techmate.DTO.RegisterRequest;
import com.techmate.techmate.Entity.Role;
import com.techmate.techmate.Entity.Usuario;
import com.techmate.techmate.Entity.VerificationToken;
import com.techmate.techmate.Repository.RoleRepository;
import com.techmate.techmate.Repository.UsuarioRepository;
import com.techmate.techmate.Repository.VerificationTokenRepository;
import com.techmate.techmate.Service.EmailService;

@RestController
public class AuthController {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.findOneByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }

        // Crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setUser_name(registerRequest.getUser_name()); // Usando el nuevo campo
        usuario.setFirst_name(registerRequest.getFirst_name()); // Usando el nuevo campo
        usuario.setLast_name(registerRequest.getLast_name()); // Usando el nuevo campo
        usuario.setEmail(registerRequest.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Guardar el usuario en la base de datos
        usuarioRepository.save(usuario);
        // Generar el token de verificación
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, usuario);
        verificationTokenRepository.save(verificationToken);

        // Enviar el correo de verificación
        String verificationUrl = "http://localhost:8080/verify?token=" + token;
        emailService.sendEmail(usuario.getEmail(), "Verificación de cuenta",
                "Por favor, verifica tu cuenta haciendo clic en el siguiente enlace: " + verificationUrl);

        return ResponseEntity.ok("Usuario registrado con éxito. Revisa tu correo para verificar tu cuenta.");
    }

}
