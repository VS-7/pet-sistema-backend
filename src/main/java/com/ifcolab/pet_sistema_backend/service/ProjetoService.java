package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoRequest;
import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoResponse;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import com.ifcolab.pet_sistema_backend.exception.ProjetoNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.exception.UsuarioNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.model.log.LogAtividade;
import com.ifcolab.pet_sistema_backend.model.log.TipoAcao;
import com.ifcolab.pet_sistema_backend.model.projeto.Projeto;
import com.ifcolab.pet_sistema_backend.model.projeto.StatusProjeto;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.ProjetoRepository;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogAtividadeService logAtividadeService;

    @Transactional
    public ProjetoResponse criar(ProjetoRequest request, Usuario usuarioLogado) {
        var tutor = usuarioRepository.findById(request.getTutorId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Tutor não encontrado"));

        var participantes = request.getParticipantesIds() != null ?
                usuarioRepository.findAllById(request.getParticipantesIds()) :
                Set.<Usuario>of();

        var projeto = Projeto.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .status(StatusProjeto.EM_DESENVOLVIMENTO)
                .tutor(tutor)
                .participantes(Set.copyOf(participantes))
                .build();

        var projetoCriado = projetoRepository.save(projeto);

        Map<String, Object> detalhesLog = Map.of(
            "titulo", projeto.getTitulo(),
            "descricao", projeto.getDescricao(),
            "tutorId", projeto.getTutor().getId(),
            "status", projeto.getStatus()
        );

        logAtividadeService.registrar(
                usuarioLogado,
                "Projeto",
                projetoCriado.getId(),
                TipoAcao.CRIAR,
                detalhesLog
        );

        return converterParaResponse(projetoCriado);
    }

    private ProjetoResponse converterParaResponse(Projeto projeto) {
        return ProjetoResponse.builder()
                .id(projeto.getId())
                .titulo(projeto.getTitulo())
                .descricao(projeto.getDescricao())
                .status(projeto.getStatus())
                .tutor(converterUsuarioParaResponse(projeto.getTutor()))
                .participantes(projeto.getParticipantes().stream()
                        .map(this::converterUsuarioParaResponse)
                        .collect(Collectors.toSet()))
                .dataCriacao(projeto.getDataCriacao())
                .dataAtualizacao(projeto.getDataAtualizacao())
                .build();
    }

    private UsuarioResponse converterUsuarioParaResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .tipo(usuario.getTipo())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<ProjetoResponse> listar(Pageable pageable) {
        return projetoRepository.findAll(pageable)
                .map(this::converterParaResponse);
    }

    @Transactional(readOnly = true)
    public ProjetoResponse buscarPorId(Long id) {
        return projetoRepository.findById(id)
                .map(this::converterParaResponse)
                .orElseThrow(() -> new ProjetoNaoEncontradoException(id));
    }

    @Transactional
    public ProjetoResponse atualizar(Long id, ProjetoRequest request, Usuario usuarioLogado) {
        var projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new ProjetoNaoEncontradoException(id));

        var tutor = usuarioRepository.findById(request.getTutorId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Tutor não encontrado"));

        Set<Usuario> participantes = new HashSet<>();
        if (request.getParticipantesIds() != null) {
            for (Long participanteId : request.getParticipantesIds()) {
                Usuario usuario = usuarioRepository.findById(participanteId)
                        .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado com ID: " + participanteId));
                participantes.add(usuario);
            }
        }

        projeto.setTitulo(request.getTitulo());
        projeto.setDescricao(request.getDescricao());
        projeto.setTutor(tutor);
        projeto.setParticipantes(participantes);

        var projetoAtualizado = projetoRepository.save(projeto);

        logAtividadeService.registrar(
                usuarioLogado,
                "Projeto",
                projetoAtualizado.getId(),
                TipoAcao.ATUALIZAR,
                null
        );

        return converterParaResponse(projetoAtualizado);
    }

    @Transactional
    public void excluir(Long id, Usuario usuarioLogado) {
        var projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new ProjetoNaoEncontradoException(id));

        projetoRepository.delete(projeto);

        logAtividadeService.registrar(
                usuarioLogado,
                "Projeto",
                id,
                TipoAcao.EXCLUIR,
                null
        );
    }
} 