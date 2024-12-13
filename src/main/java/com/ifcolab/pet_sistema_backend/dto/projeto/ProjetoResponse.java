package com.ifcolab.pet_sistema_backend.dto.projeto;

import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import com.ifcolab.pet_sistema_backend.model.projeto.StatusProjeto;
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
public class ProjetoResponse {
    private Long id;
    private String titulo;
    private String descricao;
    private StatusProjeto status;
    private UsuarioResponse tutor;
    private Set<UsuarioResponse> participantes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
} 