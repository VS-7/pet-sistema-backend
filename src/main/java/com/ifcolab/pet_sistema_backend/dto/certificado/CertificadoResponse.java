package com.ifcolab.pet_sistema_backend.dto.certificado;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificadoResponse {
    private Long id;
    private Long projetoId;
    private String projetoTitulo;
    private Long usuarioId;
    private String usuarioNome;
    private String codigo;
    private String qrCode;
    
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataEmissao;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataInicioProjeto;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime dataFimProjeto;
    
    private String descricaoAtividades;
    private Integer cargaHoraria;
} 