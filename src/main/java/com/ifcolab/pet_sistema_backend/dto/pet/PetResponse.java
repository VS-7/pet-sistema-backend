package com.ifcolab.pet_sistema_backend.dto.pet;

import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {
    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private UsuarioResponse tutor;
    private Set<UsuarioResponse> membros;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
} 