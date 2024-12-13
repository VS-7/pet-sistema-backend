package com.ifcolab.pet_sistema_backend.dto.projeto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoRequest {
    
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    @NotBlank(message = "A descrição é obrigatória")
    private String descricao;
    
    @NotNull(message = "O ID do tutor é obrigatório")
    private Long tutorId;
    
    private Set<Long> participantesIds;
} 