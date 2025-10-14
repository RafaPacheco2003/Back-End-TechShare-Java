package com.techmate.techmate.Service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.dto.RegisterRequest;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.UsuarioRole;
import com.techmate.techmate.entity.Role;
import com.techmate.techmate.entity.VerificationToken;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.VerificationTokenRepository;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.UsuarioRoleRepository;
import com.techmate.techmate.Service.mapper.AuthMapper;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    
    private final UsuarioRepository usuarioRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final EmailService emailService;
    private final EmailTemplateService emailTemplateService;
    private final AuthMapper authMapper;
    private final AppProperties appProperties;

    public AuthService(UsuarioRepository usuarioRepository,
            VerificationTokenRepository verificationTokenRepository,
            RoleRepository roleRepository,
            UsuarioRoleRepository usuarioRoleRepository,
            EmailService emailService,
            EmailTemplateService emailTemplateService,
            AuthMapper authMapper,
            AppProperties appProperties) {
        this.usuarioRepository = usuarioRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.roleRepository = roleRepository;
        this.usuarioRoleRepository = usuarioRoleRepository;
        this.emailService = emailService;
        this.emailTemplateService = emailTemplateService;
        this.authMapper = authMapper;
        this.appProperties = appProperties;
    }

    public String registerUser(RegisterRequest registerRequest) {
        if (usuarioRepository.findOneByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        Usuario usuario = authMapper.toEntity(registerRequest);

        // Guardar el usuario primero para obtener su ID
        usuarioRepository.save(usuario);
        
        // Asignar automáticamente el rol "user" (ID: 2) a todos los nuevos usuarios
        Role userRole = roleRepository.findById(2)
            .orElseThrow(() -> new RuntimeException("Rol 'user' no encontrado en la base de datos"));
        
        UsuarioRole usuarioRole = new UsuarioRole();
        usuarioRole.setUsuario(usuario);
        usuarioRole.setRole(userRole);
        usuarioRoleRepository.save(usuarioRole);
        
        log.info("Rol 'user' asignado automáticamente al usuario: {}", usuario.getEmail());

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, usuario);
        verificationTokenRepository.save(verificationToken);
        
        String verificationUrl = appProperties.getVerification().getUrl() + token;
        
        // Generar plantilla HTML profesional para el email
        String userName = usuario.getFirst_name() != null ? usuario.getFirst_name() : usuario.getUser_name();
        
        try {
            log.info("Generando email de verificación para usuario: {}", userName);
            String htmlContent = emailTemplateService.generateVerificationEmail(
                userName, 
                verificationUrl
            );
            
            log.info("Enviando email HTML a: {}", usuario.getEmail());
            emailService.sendHtmlEmail(
                usuario.getEmail(), 
                "Verificación de cuenta - TechShare", 
                htmlContent
            );
            log.info("Email enviado exitosamente a: {}", usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al enviar email HTML, intentando fallback a texto plano", e);
            try {
                emailService.sendEmail(
                    usuario.getEmail(), 
                    "Verificación de cuenta",
                    "Por favor, verifica tu cuenta haciendo clic en el siguiente enlace: " + verificationUrl
                );
                log.info("Email de texto plano enviado exitosamente a: {}", usuario.getEmail());
            } catch (Exception fallbackException) {
                log.error("Error crítico: No se pudo enviar email ni en HTML ni en texto plano", fallbackException);
                throw new RuntimeException("No se pudo enviar el email de verificación. Por favor, contacta al administrador.", fallbackException);
            }
        }

        return "Usuario registrado con éxito. Revisa tu correo para verificar tu cuenta.";
    }
}
