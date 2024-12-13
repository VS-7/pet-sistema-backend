package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.estatistica.EstatisticasResponse;
import com.ifcolab.pet_sistema_backend.repository.CertificadoRepository;
import com.ifcolab.pet_sistema_backend.repository.DocumentoRepository;
import com.ifcolab.pet_sistema_backend.repository.ProjetoRepository;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstatisticasService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;
    private final DocumentoRepository documentoRepository;
    private final CertificadoRepository certificadoRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "estatisticas", key = "'geral'")
    public EstatisticasResponse gerarEstatisticasGerais() {
        var projetos = projetoRepository.findAll();
        var now = LocalDateTime.now();

        return EstatisticasResponse.builder()
                .totalProjetos(projetoRepository.count())
                .totalUsuarios(usuarioRepository.count())
                .totalCertificados(certificadoRepository.count())
                .totalDocumentos(documentoRepository.count())
                .projetosPorStatus(projetos.stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getStatus(),
                                Collectors.counting())))
                .projetosPorTutor(projetos.stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getTutor().getNome(),
                                Collectors.counting())))
                .documentosPorTipo(documentoRepository.findAll().stream()
                        .collect(Collectors.groupingBy(
                                d -> d.getTipo().name(),
                                Collectors.counting())))
                .projetosPorAno(projetos.stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getDataCriacao().getYear(),
                                Collectors.counting())))
                .build();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "estatisticas", key = "'tutor:' + #tutorId")
    public EstatisticasResponse gerarEstatisticasPorTutor(Long tutorId) {
        var projetosTutor = projetoRepository.findByTutor(
                usuarioRepository.getReferenceById(tutorId), null).getContent();

        return EstatisticasResponse.builder()
                .totalProjetos(projetosTutor.size())
                .totalCertificados(certificadoRepository.countByProjetoIn(projetosTutor))
                .totalDocumentos(documentoRepository.countByProjetoIn(projetosTutor))
                .projetosPorStatus(projetosTutor.stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getStatus(),
                                Collectors.counting())))
                .projetosPorAno(projetosTutor.stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getDataCriacao().getYear(),
                                Collectors.counting())))
                .build();
    }
} 