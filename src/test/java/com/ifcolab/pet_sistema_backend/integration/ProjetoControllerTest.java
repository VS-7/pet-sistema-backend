package com.ifcolab.pet_sistema_backend.integration;

import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoRequest;
import com.ifcolab.pet_sistema_backend.model.projeto.StatusProjeto;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjetoControllerTest extends IntegrationTestBase {

    private TestUsuario usuarioTutor;
    private TestUsuario usuarioPetiano;
    private String tokenTutor;
    private String tokenPetiano;

    @BeforeEach
    void setUp() throws Exception {
        usuarioTutor = criarUsuarioEObterToken("tutor@example.com", TipoUsuario.TUTOR);
        usuarioPetiano = criarUsuarioEObterToken("petiano@example.com", TipoUsuario.PETIANO);
        tokenTutor = usuarioTutor.getToken();
        tokenPetiano = usuarioPetiano.getToken();
    }

    @Test
    void criar_DeveRetornarSucesso_QuandoDadosValidos() throws Exception {
        var request = ProjetoRequest.builder()
                .titulo("Projeto Teste")
                .descricao("Descrição do projeto teste")
                .tutorId(usuarioTutor.getId())
                .participantesIds(Set.of(usuarioPetiano.getId()))
                .build();

        mockMvc.perform(post("/api/v1/projetos")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Projeto Teste"))
                .andExpect(jsonPath("$.status").value("EM_DESENVOLVIMENTO"))
                .andExpect(jsonPath("$.tutor.id").value(usuarioTutor.getId()))
                .andExpect(jsonPath("$.participantes", hasSize(1)));
    }

    @Test
    void listar_DeveRetornarListaPaginada() throws Exception {
        // Criar alguns projetos de teste
        criarProjetoTeste("Projeto 1");
        criarProjetoTeste("Projeto 2");

        mockMvc.perform(get("/api/v1/projetos")
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.totalElements").exists());
    }

    @Test
    void buscarPorId_DeveRetornarProjeto_QuandoExiste() throws Exception {
        var projetoRequest = ProjetoRequest.builder()
                .titulo("Projeto para Busca")
                .descricao("Descrição do projeto")
                .tutorId(usuarioTutor.getId())
                .build();

        var resultado = mockMvc.perform(post("/api/v1/projetos")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projetoRequest)))
                .andReturn();

        var response = objectMapper.readTree(resultado.getResponse().getContentAsString());
        var projetoId = response.get("id").asLong();

        mockMvc.perform(get("/api/v1/projetos/{id}", projetoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projetoId))
                .andExpect(jsonPath("$.titulo").value("Projeto para Busca"));
    }

    @Test
    void atualizar_DeveRetornarSucesso_QuandoDadosValidos() throws Exception {
        // Primeiro criar um projeto
        var projetoId = criarProjetoTeste("Projeto Original");

        var requestAtualizacao = ProjetoRequest.builder()
                .titulo("Projeto Atualizado")
                .descricao("Nova descrição")
                .tutorId(usuarioTutor.getId())
                .participantesIds(Set.of(usuarioPetiano.getId()))
                .build();

        mockMvc.perform(put("/api/v1/projetos/{id}", projetoId)
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestAtualizacao)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Projeto Atualizado"))
                .andExpect(jsonPath("$.descricao").value("Nova descrição"));
    }

    @Test
    void excluir_DeveRetornarSucesso_QuandoProjetoExiste() throws Exception {
        var projetoId = criarProjetoTeste("Projeto para Exclusão");

        mockMvc.perform(delete("/api/v1/projetos/{id}", projetoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verificar se o projeto foi realmente excluído
        mockMvc.perform(get("/api/v1/projetos/{id}", projetoId)
                .header("Authorization", tokenPetiano))
                .andExpect(status().isNotFound());
    }

    private Long criarProjetoTeste(String titulo) throws Exception {
        var request = ProjetoRequest.builder()
                .titulo(titulo)
                .descricao("Descrição do " + titulo)
                .tutorId(usuarioTutor.getId())
                .build();

        var resultado = mockMvc.perform(post("/api/v1/projetos")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        var response = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return response.get("id").asLong();
    }
} 