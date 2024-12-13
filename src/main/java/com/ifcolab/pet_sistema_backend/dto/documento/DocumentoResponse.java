package com.ifcolab.pet_sistema_backend.dto.documento;

import com.ifcolab.pet_sistema_backend.model.documento.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoResponse {
    private Long id;
    private Long projetoId;
    private TipoDocumento tipo;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
} 