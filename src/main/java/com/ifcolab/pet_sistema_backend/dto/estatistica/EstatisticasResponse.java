package com.ifcolab.pet_sistema_backend.dto.estatistica;

import com.ifcolab.pet_sistema_backend.model.projeto.StatusProjeto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasResponse {
    private long totalProjetos;
    private long totalUsuarios;
    private long totalCertificados;
    private long totalDocumentos;
    private Map<StatusProjeto, Long> projetosPorStatus;
    private Map<String, Long> projetosPorTutor;
    private Map<String, Long> documentosPorTipo;
    private Map<Integer, Long> projetosPorAno;
} 