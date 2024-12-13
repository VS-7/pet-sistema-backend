package com.ifcolab.pet_sistema_backend.dto.documento;

import com.ifcolab.pet_sistema_backend.model.documento.TipoDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoRequest {
    
    @NotNull(message = "O ID do projeto é obrigatório")
    private Long projetoId;
    
    @NotNull(message = "O tipo do documento é obrigatório")
    private TipoDocumento tipo;
    
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    private String conteudo;
} 