package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.estatistica.EstatisticasResponse;
import com.ifcolab.pet_sistema_backend.service.EstatisticasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/estatisticas")
@RequiredArgsConstructor
@Tag(name = "Estatísticas", description = "Endpoints para geração de estatísticas do sistema")
@SecurityRequirement(name = "bearerAuth")
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Estatísticas gerais", description = "Retorna estatísticas gerais do sistema")
    public ResponseEntity<EstatisticasResponse> gerarEstatisticasGerais() {
        return ResponseEntity.ok(estatisticasService.gerarEstatisticasGerais());
    }

    @GetMapping("/tutor/{tutorId}")
    @PreAuthorize("hasRole('TUTOR')")
    @Operation(summary = "Estatísticas por tutor", description = "Retorna estatísticas dos projetos de um tutor")
    public ResponseEntity<EstatisticasResponse> gerarEstatisticasPorTutor(@PathVariable Long tutorId) {
        return ResponseEntity.ok(estatisticasService.gerarEstatisticasPorTutor(tutorId));
    }
} 