package com.ifcolab.pet_sistema_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifcolab.pet_sistema_backend.dto.auth.LoginRequest;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected TestUsuario criarUsuarioTeste(String email, TipoUsuario tipo) {
        var usuario = TestUsuario.builder()
                .nome("Usuário Teste")
                .email(email)
                .senha(passwordEncoder.encode("senha123"))
                .tipo(tipo)
                .build();
        return (TestUsuario) usuarioRepository.save(usuario);
    }

    protected String obterTokenAutenticacao(String email, String senha) throws Exception {
        var loginRequest = LoginRequest.builder()
                .email(email)
                .senha(senha)
                .build();

        var result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        var response = objectMapper.readTree(result.getResponse().getContentAsString());
        return "Bearer " + response.get("access_token").asText();
    }

    protected TestUsuario criarUsuarioEObterToken(String email, TipoUsuario tipo) throws Exception {
        var usuario = criarUsuarioTeste(email, tipo);
        usuario.setToken(obterTokenAutenticacao(email, "senha123"));
        return usuario;
    }
} 