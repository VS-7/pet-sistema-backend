package com.ifcolab.pet_sistema_backend.integration;

import com.ifcolab.pet_sistema_backend.dto.certificado.CertificadoRequest;
import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoRequest;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CertificadoControllerTest extends IntegrationTestBase {

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
    void emitir_DeveRetornarSucesso_QuandoDadosValidos() throws Exception {
        var request = CertificadoRequest.builder()
                .projetoId(projetoId)
                .usuarioId(usuarioPetiano.getId())
                .dataInicioProjeto(LocalDateTime.now().minusMonths(6))
                .dataFimProjeto(LocalDateTime.now())
                .descricaoAtividades("Participação ativa no desenvolvimento do projeto")
                .cargaHoraria(120)
                .build();

        mockMvc.perform(post("/api/v1/certificados")
                .header("Authorization", tokenTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.codigo").exists())
                .andExpect(jsonPath("$.qrCode").exists())
                .andExpect(jsonPath("$.projetoId").value(projetoId))
                .andExpect(jsonPath("$.usuarioId").value(usuarioPetiano.getId()));
    }

    @Test
    void emitir_DeveRetornarErro_QuandoNaoForTutor() throws Exception {
        var request = CertificadoRequest.builder()
                .projetoId(projetoId)
                .usuarioId(usuarioPetiano.getId())
                .dataInicioProjeto(LocalDateTime.now().minusMonths(6))
                .dataFimProjeto(LocalDateTime.now())
                .descricaoAtividades("Participação ativa no desenvolvimento do projeto")
                .cargaHoraria(120)
                .build();

        mockMvc.perform(post("/api/v1/certificados")
                .header("Authorization", tokenPetiano)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void buscarPorId_DeveRetornarCertificado_QuandoExiste() throws Exception {
        // Primeiro criar um certificado
        var certificadoId = criarCertificadoTeste();

        mockMvc.perform(get("/api/v1/certificados/{id}", certificadoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(certificadoId))
                .andExpect(jsonPath("$.projetoId").value(projetoId))
                .andExpect(jsonPath("$.usuarioId").value(usuarioPetiano.getId()));
    }

    @Test
    void validar_DeveRetornarCertificado_QuandoCodigoValido() throws Exception {
        // Primeiro criar um certificado
        var certificadoId = criarCertificadoTeste();
        var codigo = obterCodigoCertificado(certificadoId);

        mockMvc.perform(get("/api/v1/certificados/validar/{codigo}", codigo))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(codigo));
    }

    @Test
    void listarPorUsuario_DeveRetornarLista() throws Exception {
        // Criar um certificado primeiro
        criarCertificadoTeste();

        mockMvc.perform(get("/api/v1/certificados/usuario/{usuarioId}", usuarioPetiano.getId())
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].usuarioId").value(usuarioPetiano.getId()));
    }

    @Test
    void baixarPdf_DeveRetornarPDF_QuandoCertificadoExiste() throws Exception {
        // Primeiro criar um certificado
        var certificadoId = criarCertificadoTeste();

        mockMvc.perform(get("/api/v1/certificados/{id}/pdf", certificadoId)
                .header("Authorization", tokenPetiano))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, containsString("certificado-" + certificadoId + ".pdf")));
    }

    private Long criarCertificadoTeste() throws Exception {
        var request = CertificadoRequest.builder()
                .projetoId(projetoId)
                .usuarioId(usuarioPetiano.getId())
                .dataInicioProjeto(LocalDateTime.now().minusMonths(6))
                .dataFimProjeto(LocalDateTime.now())
                .descricaoAtividades("Participação ativa no desenvolvimento do projeto")
                .cargaHoraria(120)
                .build();

        var resultado = mockMvc.perform(post("/api/v1/certificados")
                .header("Authorization", tokenTutor)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        var response = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return response.get("id").asLong();
    }

    private String obterCodigoCertificado(Long certificadoId) throws Exception {
        var resultado = mockMvc.perform(get("/api/v1/certificados/{id}", certificadoId)
                .header("Authorization", tokenPetiano))
                .andReturn();

        var response = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return response.get("codigo").asText();
    }

    private Long criarProjetoTeste() throws Exception {
        var request = ProjetoRequest.builder()
                .titulo("Projeto para Certificado")
                .descricao("Projeto para testes de certificado")
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