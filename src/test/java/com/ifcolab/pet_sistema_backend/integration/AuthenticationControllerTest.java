package com.ifcolab.pet_sistema_backend.integration;

import com.ifcolab.pet_sistema_backend.dto.auth.LoginRequest;
import com.ifcolab.pet_sistema_backend.dto.auth.RegisterRequest;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends IntegrationTestBase {

    @Test
    void registrar_DeveRetornarSucesso_QuandoDadosValidos() throws Exception {
        var request = RegisterRequest.builder()
                .nome("Novo Usuário")
                .email("novo@example.com")
                .senha("senha123")
                .tipo(TipoUsuario.PETIANO)
                .build();

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    void registrar_DeveRetornarErro_QuandoEmailJaExiste() throws Exception {
        criarUsuarioTeste("existente@example.com", TipoUsuario.PETIANO);

        var request = RegisterRequest.builder()
                .nome("Novo Usuário")
                .email("existente@example.com")
                .senha("senha123")
                .tipo(TipoUsuario.PETIANO)
                .build();

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Email já cadastrado: existente@example.com"));
    }

    @Test
    void login_DeveRetornarSucesso_QuandoCredenciaisValidas() throws Exception {
        criarUsuarioTeste("login@example.com", TipoUsuario.PETIANO);

        var request = LoginRequest.builder()
                .email("login@example.com")
                .senha("senha123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    void login_DeveRetornarErro_QuandoCredenciaisInvalidas() throws Exception {
        var request = LoginRequest.builder()
                .email("naoexiste@example.com")
                .senha("senha123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Credenciais inválidas"));
    }

    @Test
    void registrar_DeveRetornarErro_QuandoDadosInvalidos() throws Exception {
        var request = RegisterRequest.builder()
                .nome("")
                .email("emailinvalido")
                .senha("")
                .build();

        mockMvc.perform(post("/api/v1/auth/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação"))
                .andExpect(jsonPath("$.erros").exists());
    }
} 