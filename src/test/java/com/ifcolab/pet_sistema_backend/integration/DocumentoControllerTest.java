package com.ifcolab.pet_sistema_backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifcolab.pet_sistema_backend.dto.documento.DocumentoRequest;
import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoRequest;
import com.ifcolab.pet_sistema_backend.model.documento.TipoDocumento;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentoControllerTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    private TestUsuario usuarioTutor;
    private TestUsuario usuarioPetiano;
    private String tokenTutor;
    private String tokenPetiano;
    private Long projetoId;

    @BeforeEach
    void setUp() throws Exception {
        usuarioTutor = criarUsuarioEObterToken("tutor@example.com", TipoUsuario.TUTOR);
        usuarioPetiano = criarUsuarioEObterToken("petiano@example.com", TipoUsuario.PETIANO);
        tokenTutor = usuarioTutor.getToken();
        tokenPetiano = usuarioPetiano.getToken();
        
        // Criar um projeto para os testes
        projetoId = criarProjetoTeste();
    }

    @Test
    void criar_DeveRetornarSucesso_QuandoDadosValidos() throws Exception {
        // Criar um objeto JsonNode para o conteúdo
        JsonNode conteudoJson = objectMapper.readTree("{\"key\": \"value\"}");

        var request = DocumentoRequest.builder()
                .projetoId(projetoId)
                .tipo(TipoDocumento.ENSINO)
                .titulo("Documento de Teste")
                .conteudo(conteudoJson)
                .build();

        mockMvc.perform(post("/api/v1/documentos")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Documento de Teste"))
                .andExpect(jsonPath("$.tipo").value("ENSINO"));
    }

    @Test
    void buscarPorId_DeveRetornarDocumento_QuandoExiste() throws Exception {
        var documentoId = criarDocumentoTeste();

        mockMvc.perform(get("/api/v1/documentos/{id}", documentoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(documentoId))
                .andExpect(jsonPath("$.titulo").value("Documento Teste"));
    }

    @Test
    void atualizar_DeveRetornarSucesso_QuandoDadosValidos() throws Exception {
        var documentoId = criarDocumentoTeste();

        var request = DocumentoRequest.builder()
                .projetoId(projetoId)
                .tipo(TipoDocumento.PESQUISA)
                .titulo("Documento Atualizado")
                .conteudo(objectMapper.readTree("{\"key\": \"value\"}"))
                .build();

        mockMvc.perform(put("/api/v1/documentos/{id}", documentoId)
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Documento Atualizado"))
                .andExpect(jsonPath("$.tipo").value("PESQUISA"));
    }

    @Test
    void excluir_DeveRetornarSucesso_QuandoDocumentoExiste() throws Exception {
        var documentoId = criarDocumentoTeste();

        mockMvc.perform(delete("/api/v1/documentos/{id}", documentoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verificar se o documento foi realmente excluído
        mockMvc.perform(get("/api/v1/documentos/{id}", documentoId)
                .header("Authorization", tokenPetiano))
                .andExpect(status().isNotFound());
    }

    private Long criarProjetoTeste() throws Exception {
        var request = ProjetoRequest.builder()
                .titulo("Projeto para Documentos")
                .descricao("Projeto para testes de documentos")
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

    private Long criarDocumentoTeste() throws Exception {
        var request = DocumentoRequest.builder()
                .projetoId(projetoId)
                .tipo(TipoDocumento.ENSINO)
                .titulo("Documento Teste")
                .conteudo(objectMapper.readTree("{\"key\": \"value\"}"))
                .build();

        var resultado = mockMvc.perform(post("/api/v1/documentos")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        var response = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return response.get("id").asLong();
    }
} 