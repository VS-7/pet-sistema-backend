package com.ifcolab.pet_sistema_backend.dto.pet;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetRequest {
    @NotBlank(message = "O nome do PET é obrigatório")
    private String nome;
    
    private String descricao;
    
    private Set<Long> membrosIds;
} 