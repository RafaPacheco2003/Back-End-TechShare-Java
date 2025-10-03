package com.techmate.techmate.Service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.techmate.techmate.dto.RegisterRequest;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.VerificationToken;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.VerificationTokenRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.Service.mapper.AuthMapper;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final AuthMapper authMapper;
    private final String verificationBaseUrl;

    public AuthService(UsuarioRepository usuarioRepository,
            VerificationTokenRepository verificationTokenRepository,
            RoleRepository roleRepository,
            EmailService emailService,
            AuthMapper authMapper,
            @Value("${app.verification.url:http://localhost:8080/verify?token=}") String verificationBaseUrl) {
        this.usuarioRepository = usuarioRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.authMapper = authMapper;
        this.verificationBaseUrl = verificationBaseUrl;
    }

    public String registerUser(RegisterRequest registerRequest) {
        if (usuarioRepository.findOneByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        Usuario usuario = authMapper.toEntity(registerRequest);

        usuarioRepository.save(usuario);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, usuario);
        verificationTokenRepository.save(verificationToken);

    String verificationUrl = verificationBaseUrl + token;
        emailService.sendEmail(usuario.getEmail(), "Verificación de cuenta",
                "Por favor, verifica tu cuenta haciendo clic en el siguiente enlace: " + verificationUrl);

        return "Usuario registrado con éxito. Revisa tu correo para verificar tu cuenta.";
    }
}
