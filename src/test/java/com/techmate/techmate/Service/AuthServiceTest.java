package com.techmate.techmate.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import com.techmate.techmate.Service.mapper.AuthMapper;
import com.techmate.techmate.config.AppProperties;
import com.techmate.techmate.dto.RegisterRequest;
import com.techmate.techmate.entity.Usuario;
import com.techmate.techmate.entity.VerificationToken;
import com.techmate.techmate.repository.RoleRepository;
import com.techmate.techmate.repository.UsuarioRepository;
import com.techmate.techmate.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class AuthServiceTest {

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    VerificationTokenRepository verificationTokenRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    EmailService emailService;

    @Mock
    AppProperties appProperties;

    AuthMapper authMapper = new AuthMapper();

    AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock AppProperties and its nested configuration
        AppProperties.Verification verification = new AppProperties.Verification();
        verification.setUrl("http://localhost:8080/verify?token=");
        when(appProperties.getVerification()).thenReturn(verification);
        
        authService = new AuthService(usuarioRepository, verificationTokenRepository, roleRepository, emailService, authMapper, appProperties);
    }

    @Test
    void registerUser_success() {
        RegisterRequest req = new RegisterRequest();
        req.setUser_name("jdoe");
        req.setFirst_name("John");
        req.setLast_name("Doe");
        req.setEmail("jdoe@example.com");
        req.setPassword("encoded");

        when(usuarioRepository.findOneByEmail(any())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(i -> i.getArgument(0));

        String res = authService.registerUser(req);

        assertTrue(res.contains("Usuario registrado"));
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(verificationTokenRepository, times(1)).save(any(VerificationToken.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void registerUser_duplicate_throws() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("exists@example.com");

        when(usuarioRepository.findOneByEmail(any())).thenReturn(Optional.of(new Usuario()));

        assertThrows(IllegalArgumentException.class, () -> authService.registerUser(req));
    }
}
