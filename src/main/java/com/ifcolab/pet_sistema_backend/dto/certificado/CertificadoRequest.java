package com.ifcolab.pet_sistema_backend.dto.certificado;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoRequest {
    
    @NotNull(message = "O ID do projeto é obrigatório")
    private Long projetoId;
    
    @NotNull(message = "O ID do usuário é obrigatório")
    private Long usuarioId;
    
    @NotNull(message = "A data de início do projeto é obrigatória")
    private LocalDateTime dataInicioProjeto;
    
    @NotNull(message = "A data de fim do projeto é obrigatória")
    private LocalDateTime dataFimProjeto;
    
    @NotBlank(message = "A descrição das atividades é obrigatória")
    private String descricaoAtividades;
    
    @NotNull(message = "A carga horária é obrigatória")
    @Min(value = 1, message = "A carga horária deve ser maior que zero")
    private Integer cargaHoraria;
} 