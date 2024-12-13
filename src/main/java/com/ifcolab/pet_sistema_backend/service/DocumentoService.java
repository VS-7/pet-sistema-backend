package com.ifcolab.pet_sistema_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifcolab.pet_sistema_backend.dto.documento.DocumentoRequest;
import com.ifcolab.pet_sistema_backend.dto.documento.DocumentoResponse;
import com.ifcolab.pet_sistema_backend.exception.DocumentoNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.exception.ProjetoNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.exception.TituloDocumentoDuplicadoException;
import com.ifcolab.pet_sistema_backend.model.documento.Documento;
import com.ifcolab.pet_sistema_backend.model.log.TipoAcao;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.DocumentoRepository;
import com.ifcolab.pet_sistema_backend.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
     
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final ProjetoRepository projetoRepository;
    private final LogAtividadeService logAtividadeService;

    @Transactional
    public DocumentoResponse criar(DocumentoRequest request, Usuario usuarioLogado) {
        var projeto = projetoRepository.findById(request.getProjetoId())
                .orElseThrow(() -> new ProjetoNaoEncontradoException(request.getProjetoId()));

        if (documentoRepository.existsByProjetoAndTitulo(projeto, request.getTitulo())) {
            throw new TituloDocumentoDuplicadoException(request.getTitulo());
        }

        try {
            JsonNode conteudoJson = request.getConteudo();

            var documento = Documento.builder()
                    .projeto(projeto)
                    .tipo(request.getTipo())
                    .titulo(request.getTitulo())
                    .conteudo(conteudoJson)
                    .build();

            var documentoSalvo = documentoRepository.save(documento);

            logAtividadeService.registrar(
                    usuarioLogado,
                    "Documento",
                    documentoSalvo.getId(),
                    TipoAcao.CRIAR,
                    null
            );

            return converterParaResponse(documentoSalvo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar documento", e);
        }
    }

    @Transactional(readOnly = true)
    public DocumentoResponse buscarPorId(Long id) {
        return documentoRepository.findById(id)
                .map(this::converterParaResponse)
                .orElseThrow(() -> new DocumentoNaoEncontradoException(id));
    }

    @Transactional
    public DocumentoResponse atualizar(Long id, DocumentoRequest request, Usuario usuarioLogado) {
        var documento = documentoRepository.findById(id)
                .orElseThrow(() -> new DocumentoNaoEncontradoException(id));

        if (!documento.getTitulo().equals(request.getTitulo()) &&
                documentoRepository.existsByProjetoAndTitulo(documento.getProjeto(), request.getTitulo())) {
            throw new TituloDocumentoDuplicadoException(request.getTitulo());
        }

        documento.setTipo(request.getTipo());
        documento.setTitulo(request.getTitulo());
        documento.setConteudo(request.getConteudo());

        var documentoAtualizado = documentoRepository.save(documento);

        logAtividadeService.registrar(
                usuarioLogado,
                "Documento",
                documentoAtualizado.getId(),
                TipoAcao.ATUALIZAR,
                null
        );

        return converterParaResponse(documentoAtualizado);
    }

    @Transactional
    public void excluir(Long id, Usuario usuarioLogado) {
        var documento = documentoRepository.findById(id)
                .orElseThrow(() -> new DocumentoNaoEncontradoException(id));

        documentoRepository.delete(documento);

        logAtividadeService.registrar(
                usuarioLogado,
                "Documento",
                id,
                TipoAcao.EXCLUIR,
                null
        );
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listarTodos() {
        List<Documento> documentos = documentoRepository.findAll();
        return documentos.stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    private DocumentoResponse converterParaResponse(Documento documento) {
        return DocumentoResponse.builder()
                .id(documento.getId())
                .projetoId(documento.getProjeto().getId())
                .tipo(documento.getTipo())
                .titulo(documento.getTitulo())
                .conteudo(documento.getConteudo())
                .dataCriacao(documento.getDataCriacao())
                .dataAtualizacao(documento.getDataAtualizacao())
                .build();
    }
    
} 