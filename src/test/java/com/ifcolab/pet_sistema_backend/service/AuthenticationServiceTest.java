package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.auth.LoginRequest;
import com.ifcolab.pet_sistema_backend.dto.auth.RegisterRequest;
import com.ifcolab.pet_sistema_backend.exception.EmailJaCadastradoException;
import com.ifcolab.pet_sistema_backend.exception.UsuarioNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import com.ifcolab.pet_sistema_backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .nome("Test User")
                .email("test@example.com")
                .senha("password123")
                .tipo(TipoUsuario.PETIANO)
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .senha("password123")
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nome("Test User")
                .email("test@example.com")
                .senha("encoded_password")
                .tipo(TipoUsuario.PETIANO)
                .build();
    }

    @Test
    void registrar_DeveCriarUsuario_QuandoDadosValidos() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("token123");

        // Act
        var response = authenticationService.registrar(registerRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("token123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrar_DeveLancarExcecao_QuandoEmailJaCadastrado() {
        // Arrange
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.registrar(registerRequest))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessageContaining("Email já cadastrado");
    }

    @Test
    void autenticar_DeveRetornarToken_QuandoCredenciaisValidas() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("token123");

        // Act
        var response = authenticationService.autenticar(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("token123");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void autenticar_DeveLancarExcecao_QuandoUsuarioNaoEncontrado() {
        // Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.autenticar(loginRequest))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessageContaining("Usuário não encontrado");
    }
} 