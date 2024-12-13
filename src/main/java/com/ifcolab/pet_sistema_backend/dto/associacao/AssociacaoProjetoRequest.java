package com.ifcolab.pet_sistema_backend.dto.associacao;

import com.ifcolab.pet_sistema_backend.model.associacao.TipoAssociacao;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociacaoProjetoRequest {
    
    @NotNull(message = "O ID do projeto de origem é obrigatório")
    private Long projetoOrigemId;
    
    @NotNull(message = "O ID do projeto de destino é obrigatório")
    private Long projetoDestinoId;
    
    @NotNull(message = "O tipo de associação é obrigatório")
    private TipoAssociacao tipo;
    
    private String descricao;
} 