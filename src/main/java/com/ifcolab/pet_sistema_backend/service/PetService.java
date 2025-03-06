package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.pet.PetRequest;
import com.ifcolab.pet_sistema_backend.dto.pet.PetResponse;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import com.ifcolab.pet_sistema_backend.exception.ResourceNotFoundException;
import com.ifcolab.pet_sistema_backend.exception.UnauthorizedException;
import com.ifcolab.pet_sistema_backend.model.pet.Pet;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.PetRepository;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public PetResponse criar(PetRequest request, Usuario usuarioLogado) {
        if (usuarioLogado.getTipo() != TipoUsuario.TUTOR) {
            throw new UnauthorizedException("Apenas tutores podem criar grupos PET");
        }

        if (petRepository.existsByNome(request.getNome())) {
            throw new IllegalArgumentException("Já existe um PET com este nome");
        }

        var pet = Pet.builder()
                .nome(request.getNome())
                .descricao(request.getDescricao())
                .codigo(gerarCodigo())
                .tutor(usuarioLogado)
                .build();

        if (request.getMembrosIds() != null && !request.getMembrosIds().isEmpty()) {
            var membros = usuarioRepository.findAllById(request.getMembrosIds());
            pet.setMembros(membros.stream().collect(Collectors.toSet()));
        }

        return converterParaResponse(petRepository.save(pet));
    }

    @Transactional(readOnly = true)
    public List<PetResponse> listarPorUsuario(Usuario usuario) {
        return petRepository.findByUsuario(usuario).stream()
                .map(this::converterParaResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PetResponse buscarPorId(Long id, Usuario usuarioLogado) {
        var pet = buscarEntidade(id);
        validarAcesso(pet, usuarioLogado);
        return converterParaResponse(pet);
    }

    @Transactional
    public PetResponse atualizar(Long id, PetRequest request, Usuario usuarioLogado) {
        var pet = buscarEntidade(id);
        validarAcesso(pet, usuarioLogado);
        
        if (!pet.getNome().equals(request.getNome()) && petRepository.existsByNome(request.getNome())) {
            throw new IllegalArgumentException("Já existe um PET com este nome");
        }

        pet.setNome(request.getNome());
        pet.setDescricao(request.getDescricao());

        if (request.getMembrosIds() != null) {
            var membros = usuarioRepository.findAllById(request.getMembrosIds());
            pet.setMembros(membros.stream().collect(Collectors.toSet()));
        }

        return converterParaResponse(petRepository.save(pet));
    }

    @Transactional
    public void excluir(Long id, Usuario usuarioLogado) {
        var pet = buscarEntidade(id);
        validarAcesso(pet, usuarioLogado);
        petRepository.delete(pet);
    }

    public Pet buscarEntidade(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PET não encontrado"));
    }

    private void validarAcesso(Pet pet, Usuario usuario) {
        boolean isTutor = pet.getTutor().equals(usuario);
        boolean isMembro = pet.getMembros().contains(usuario);
        
        if (!isTutor && !isMembro) {
            throw new UnauthorizedException("Usuário não tem acesso a este PET");
        }
    }

    private String gerarCodigo() {
        String codigo;
        do {
            codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (petRepository.existsByCodigo(codigo));
        return codigo;
    }

    private PetResponse converterParaResponse(Pet pet) {
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