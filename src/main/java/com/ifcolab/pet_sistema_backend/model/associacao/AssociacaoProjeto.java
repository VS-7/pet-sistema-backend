package com.ifcolab.pet_sistema_backend.model.associacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Document(collection = "associacoes_projetos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociacaoProjeto {
    
    @Id
    private String id;
    
    private Long projetoOrigemId;
    
    private Long projetoDestinoId;
    
    private TipoAssociacao tipo;
    
    private String descricao;
    
    private LocalDateTime dataCriacao;
    
    private LocalDateTime dataAtualizacao;
} 