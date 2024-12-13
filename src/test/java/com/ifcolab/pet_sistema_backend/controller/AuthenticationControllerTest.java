package com.ifcolab.pet_sistema_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifcolab.pet_sistema_backend.config.SecurityTestConfig;
import com.ifcolab.pet_sistema_backend.dto.auth.AuthenticationResponse;
import com.ifcolab.pet_sistema_backend.dto.auth.LoginRequest;
import com.ifcolab.pet_sistema_backend.dto.auth.RegisterRequest;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@Import(SecurityTestConfig.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void registrar_DeveRetornarToken_QuandoDadosValidos() throws Exception {
        // Arrange
        var request = RegisterRequest.builder()
                .nome("Test User")
                .email("test@example.com")
                .senha("password123")
                .tipo(TipoUsuario.PETIANO)
                .build();

        var response = AuthenticationResponse.builder()
                .accessToken("token123")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();

        when(authenticationService.registrar(any(RegisterRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token123"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600));
    }

    @Test
    void login_DeveRetornarToken_QuandoCredenciaisValidas() throws Exception {
        // Arrange
        var request = LoginRequest.builder()
                .email("test@example.com")
                .senha("password123")
                .build();

        var response = AuthenticationResponse.builder()
                .accessToken("token123")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();

        when(authenticationService.autenticar(any(LoginRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token123"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(3600));
    }

    @Test
    void registrar_DeveRetornarBadRequest_QuandoDadosInvalidos() throws Exception {
        // Arrange
        var request = RegisterRequest.builder()
                .nome("")
                .email("invalid-email")
                .senha("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"));
    }
} 