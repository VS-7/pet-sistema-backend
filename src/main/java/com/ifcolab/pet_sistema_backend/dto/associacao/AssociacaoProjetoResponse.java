package com.ifcolab.pet_sistema_backend.dto.associacao;

import com.ifcolab.pet_sistema_backend.model.associacao.TipoAssociacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociacaoProjetoResponse {
    private String id;
    private Long projetoOrigemId;
    private Long projetoDestinoId;
    private TipoAssociacao tipo;
    private String descricao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
} 