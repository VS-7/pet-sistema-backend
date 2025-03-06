package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoRequest;
import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoResponse;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import com.ifcolab.pet_sistema_backend.dto.pet.PetResponse;
import com.ifcolab.pet_sistema_backend.exception.ResourceNotFoundException;
import com.ifcolab.pet_sistema_backend.exception.UnauthorizedException;

import com.ifcolab.pet_sistema_backend.model.log.TipoAcao;
import com.ifcolab.pet_sistema_backend.model.pet.Pet;
import com.ifcolab.pet_sistema_backend.model.projeto.Projeto;
import com.ifcolab.pet_sistema_backend.model.projeto.StatusProjeto;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final LogAtividadeService logAtividadeService;
    private final PetService petService;

    @Transactional
    public ProjetoResponse criar(ProjetoRequest request, Usuario usuarioLogado) {
        var pet = petService.buscarEntidade(request.getPetId());
        
        // Verifica se o usuário tem acesso ao PET
        if (!pet.getTutor().equals(usuarioLogado) && !pet.getMembros().contains(usuarioLogado)) {
            throw new UnauthorizedException("Usuário não tem acesso a este PET");
        }

        var projeto = Projeto.builder()
                .titulo(request.getTitulo())
                .descricao(request.getDescricao())
                .status(StatusProjeto.EM_ANDAMENTO)
                .tutor(pet.getTutor()) // O tutor do projeto será sempre o tutor do PET
                .pet(pet)
                .build();

        if (request.getParticipantesIds() != null && !request.getParticipantesIds().isEmpty()) {
            var participantes = pet.getMembros().stream()
                    .filter(membro -> request.getParticipantesIds().contains(membro.getId()))
                    .collect(Collectors.toSet());
            projeto.setParticipantes(participantes);
        }

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

    @Transactional(readOnly = true)
    public Page<ProjetoResponse> listar(Pageable pageable, Usuario usuarioLogado) {
        return projetoRepository.findByPetTutorOrPetMembros(usuarioLogado, pageable)
                .map(this::converterParaResponse);
    }

    @Transactional(readOnly = true)
    public ProjetoResponse buscarPorId(Long id, Usuario usuarioLogado) {
        var projeto = buscarEntidade(id);
        validarAcesso(projeto, usuarioLogado);
        return converterParaResponse(projeto);
    }

    @Transactional
    public ProjetoResponse atualizar(Long id, ProjetoRequest request, Usuario usuarioLogado) {
        var projeto = buscarEntidade(id);
        validarAcesso(projeto, usuarioLogado);

        // Verifica se o novo PET é diferente do atual
        if (!projeto.getPet().getId().equals(request.getPetId())) {
            var novoPet = petService.buscarEntidade(request.getPetId());
            validarAcesso(novoPet, usuarioLogado);
            projeto.setPet(novoPet);
        }

        projeto.setTitulo(request.getTitulo());
        projeto.setDescricao(request.getDescricao());

        if (request.getParticipantesIds() != null) {
            var participantes = projeto.getPet().getMembros().stream()
                    .filter(membro -> request.getParticipantesIds().contains(membro.getId()))
                    .collect(Collectors.toSet());
            projeto.setParticipantes(participantes);
        }

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
        var projeto = buscarEntidade(id);
        validarAcesso(projeto, usuarioLogado);
        projetoRepository.delete(projeto);

        logAtividadeService.registrar(
                usuarioLogado,
                "Projeto",
                id,
                TipoAcao.EXCLUIR,
                null
        );
    }

    public Projeto buscarEntidade(Long id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado"));
    }

    private void validarAcesso(Projeto projeto, Usuario usuario) {
        Pet pet = projeto.getPet();
        boolean isTutor = pet.getTutor().equals(usuario);
        boolean isMembro = pet.getMembros().contains(usuario);
        
        if (!isTutor && !isMembro) {
            throw new UnauthorizedException("Usuário não tem acesso a este projeto");
        }
    }

    private void validarAcesso(Pet pet, Usuario usuario) {
        boolean isTutor = pet.getTutor().equals(usuario);
        boolean isMembro = pet.getMembros().contains(usuario);
        
        if (!isTutor && !isMembro) {
            throw new UnauthorizedException("Usuário não tem acesso a este PET");
        }
    }

    private ProjetoResponse converterParaResponse(Projeto projeto) {
        return ProjetoResponse.builder()
                .id(projeto.getId())
                .titulo(projeto.getTitulo())
                .descricao(projeto.getDescricao())
                .status(projeto.getStatus())
                .pet(converterPetParaResponse(projeto.getPet()))
                .tutor(converterUsuarioParaResponse(projeto.getTutor()))
                .participantes(projeto.getParticipantes().stream()
                        .map(this::converterUsuarioParaResponse)
                        .collect(Collectors.toSet()))
                .dataCriacao(projeto.getDataCriacao())
                .dataAtualizacao(projeto.getDataAtualizacao())
                .build();
    }

    private PetResponse converterPetParaResponse(Pet pet) {
        return PetResponse.builder()
                .id(pet.getId())
                .nome(pet.getNome())
                .codigo(pet.getCodigo())
                .descricao(pet.getDescricao())
                .tutor(converterUsuarioParaResponse(pet.getTutor()))
                .membros(pet.getMembros().stream()
                        .map(this::converterUsuarioParaResponse)
                        .collect(Collectors.toSet()))
                .dataCriacao(pet.getDataCriacao())
                .dataAtualizacao(pet.getDataAtualizacao())
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
} 