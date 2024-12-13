package com.ifcolab.pet_sistema_backend.integration;

import com.ifcolab.pet_sistema_backend.dto.associacao.AssociacaoProjetoRequest;
import com.ifcolab.pet_sistema_backend.model.associacao.TipoAssociacao;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoRequest;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
class AssociacaoProjetoControllerTest extends IntegrationTestBase {

    private TestUsuario usuarioTutor;
    private TestUsuario usuarioPetiano;
    private String tokenTutor;
    private String tokenPetiano;
    private Long projetoOrigemId;
    private Long projetoDestinoId;

    @BeforeEach
    void setUp() throws Exception {
        usuarioTutor = criarUsuarioEObterToken("tutor@example.com", TipoUsuario.TUTOR);
        usuarioPetiano = criarUsuarioEObterToken("petiano@example.com", TipoUsuario.PETIANO);
        tokenTutor = usuarioTutor.getToken();
        tokenPetiano = usuarioPetiano.getToken();
        
        // Criar projetos para os testes
        projetoOrigemId = criarProjetoTeste("Projeto Origem");
        projetoDestinoId = criarProjetoTeste("Projeto Destino");
    }

    @Test
    void criar_DeveRetornarSucesso_QuandoDadosValidos() throws Exception {
        var request = AssociacaoProjetoRequest.builder()
                .projetoOrigemId(projetoOrigemId)
                .projetoDestinoId(projetoDestinoId)
                .tipo(TipoAssociacao.REFERENCIA)
                .descricao("Associação de teste")
                .build();

        mockMvc.perform(post("/api/v1/associacoes")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.projetoOrigemId").value(projetoOrigemId))
                .andExpect(jsonPath("$.projetoDestinoId").value(projetoDestinoId))
                .andExpect(jsonPath("$.tipo").value("REFERENCIA"));
    }

    @Test
    void buscarPorProjetoOrigem_DeveRetornarLista() throws Exception {
        // Criar uma associação primeiro
        criarAssociacaoTeste();

        mockMvc.perform(get("/api/v1/associacoes/origem/{projetoId}", projetoOrigemId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].projetoOrigemId").value(projetoOrigemId));
    }

    @Test
    void buscarPorProjetoDestino_DeveRetornarLista() throws Exception {
        // Criar uma associação primeiro
        criarAssociacaoTeste();

        mockMvc.perform(get("/api/v1/associacoes/destino/{projetoId}", projetoDestinoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].projetoDestinoId").value(projetoDestinoId));
    }

    @Test
    void excluir_DeveRetornarSucesso_QuandoAssociacaoExiste() throws Exception {
        // Criar uma associação primeiro
        String associacaoId = criarAssociacaoTeste();

        mockMvc.perform(delete("/api/v1/associacoes/{id}", associacaoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Verificar se a associação foi removida
        mockMvc.perform(get("/api/v1/associacoes/origem/{projetoId}", projetoOrigemId)
                .header("Authorization", tokenPetiano))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void criar_DeveRetornarErro_QuandoProjetoNaoExiste() throws Exception {
        var request = AssociacaoProjetoRequest.builder()
                .projetoOrigemId(999L)
                .projetoDestinoId(projetoDestinoId)
                .tipo(TipoAssociacao.REFERENCIA)
                .descricao("Associação inválida")
                .build();

        mockMvc.perform(post("/api/v1/associacoes")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(containsString("Projeto não encontrado")));
    }

    private String criarAssociacaoTeste() throws Exception {
        var request = AssociacaoProjetoRequest.builder()
                .projetoOrigemId(projetoOrigemId)
                .projetoDestinoId(projetoDestinoId)
                .tipo(TipoAssociacao.REFERENCIA)
                .descricao("Associação de teste")
                .build();

        var resultado = mockMvc.perform(post("/api/v1/associacoes")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        var response = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return response.get("id").asText();
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
                .andExpect(status().isCreated())
                .andReturn();

        var response = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return response.get("id").asLong();
    }
} 