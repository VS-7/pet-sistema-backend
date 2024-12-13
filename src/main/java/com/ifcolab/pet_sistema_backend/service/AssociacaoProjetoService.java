package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.associacao.AssociacaoProjetoRequest;
import com.ifcolab.pet_sistema_backend.dto.associacao.AssociacaoProjetoResponse;
import com.ifcolab.pet_sistema_backend.exception.AssociacaoNaoEncontradaException;
import com.ifcolab.pet_sistema_backend.exception.ProjetoNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.model.associacao.AssociacaoProjeto;
import com.ifcolab.pet_sistema_backend.model.log.TipoAcao;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.AssociacaoProjetoRepository;
import com.ifcolab.pet_sistema_backend.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssociacaoProjetoService {

    private final AssociacaoProjetoRepository associacaoRepository;
    private final ProjetoRepository projetoRepository;
    private final LogAtividadeService logAtividadeService;

    @Transactional
    public AssociacaoProjetoResponse criar(AssociacaoProjetoRequest request, Usuario usuarioLogado) {
        validarProjetos(request.getProjetoOrigemId(), request.getProjetoDestinoId());

        var associacao = AssociacaoProjeto.builder()
                .projetoOrigemId(request.getProjetoOrigemId())
                .projetoDestinoId(request.getProjetoDestinoId())
                .tipo(request.getTipo())
                .descricao(request.getDescricao())
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        var associacaoSalva = associacaoRepository.save(associacao);

        logAtividadeService.registrar(
                usuarioLogado,
                "AssociacaoProjeto",
                request.getProjetoOrigemId(),
                TipoAcao.CRIAR,
                "Associação criada com projeto: " + request.getProjetoDestinoId()
        );

        return converterParaResponse(associacaoSalva);
    }

    @Transactional(readOnly = true)
    public List<AssociacaoProjetoResponse> buscarPorProjetoOrigem(Long projetoId) {
        return associacaoRepository.findByProjetoOrigemId(projetoId)
                .stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssociacaoProjetoResponse> buscarPorProjetoDestino(Long projetoId) {
        return associacaoRepository.findByProjetoDestinoId(projetoId)
                .stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void excluir(String id, Usuario usuarioLogado) {
        var associacao = associacaoRepository.findById(id)
                .orElseThrow(() -> new AssociacaoNaoEncontradaException(id));

        associacaoRepository.delete(associacao);

        logAtividadeService.registrar(
                usuarioLogado,
                "AssociacaoProjeto",
                associacao.getProjetoOrigemId(),
                TipoAcao.EXCLUIR,
                "Associação removida com projeto: " + associacao.getProjetoDestinoId()
        );
    }

    private void validarProjetos(Long projetoOrigemId, Long projetoDestinoId) {
        if (!projetoRepository.existsById(projetoOrigemId)) {
            throw new ProjetoNaoEncontradoException(projetoOrigemId);
        }
        if (!projetoRepository.existsById(projetoDestinoId)) {
            throw new ProjetoNaoEncontradoException(projetoDestinoId);
        }
    }

    private AssociacaoProjetoResponse converterParaResponse(AssociacaoProjeto associacao) {
        return AssociacaoProjetoResponse.builder()
                .id(associacao.getId())
                .projetoOrigemId(associacao.getProjetoOrigemId())
                .projetoDestinoId(associacao.getProjetoDestinoId())
                .tipo(associacao.getTipo())
                .descricao(associacao.getDescricao())
                .dataCriacao(associacao.getDataCriacao())
                .dataAtualizacao(associacao.getDataAtualizacao())
                .build();
    }
} 