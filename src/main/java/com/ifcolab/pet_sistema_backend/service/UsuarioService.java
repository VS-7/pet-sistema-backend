package com.ifcolab.pet_sistema_backend.service;

import org.springframework.stereotype.Service;
import com.ifcolab.pet_sistema_backend.exception.ResourceNotFoundException;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioResponse buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .tipo(usuario.getTipo())
                .imagemPerfil(usuario.getImagemPerfil())
                .bio(usuario.getBio())
                .build();
    }
} 