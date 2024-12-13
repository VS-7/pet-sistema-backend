package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.associacao.AssociacaoProjetoRequest;
import com.ifcolab.pet_sistema_backend.dto.associacao.AssociacaoProjetoResponse;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.service.AssociacaoProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/associacoes")
@RequiredArgsConstructor
@Tag(name = "Associações", description = "Endpoints para gerenciamento de associações entre projetos")
@SecurityRequirement(name = "bearerAuth")
public class AssociacaoProjetoController {

    private final AssociacaoProjetoService associacaoService;

    @PostMapping
    @Operation(summary = "Criar associação", description = "Cria uma nova associação entre projetos")
    public ResponseEntity<AssociacaoProjetoResponse> criar(
            @RequestBody @Valid AssociacaoProjetoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var associacao = associacaoService.criar(request, usuarioLogado);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(associacao.getId())
                .toUri();
        return ResponseEntity.created(uri).body(associacao);
    }

    @GetMapping("/origem/{projetoId}")
    @Operation(summary = "Buscar por origem", description = "Lista todas as associações onde o projeto é origem")
    public ResponseEntity<List<AssociacaoProjetoResponse>> buscarPorProjetoOrigem(
            @PathVariable Long projetoId
    ) {
        return ResponseEntity.ok(associacaoService.buscarPorProjetoOrigem(projetoId));
    }

    @GetMapping("/destino/{projetoId}")
    @Operation(summary = "Buscar por destino", description = "Lista todas as associações onde o projeto é destino")
    public ResponseEntity<List<AssociacaoProjetoResponse>> buscarPorProjetoDestino(
            @PathVariable Long projetoId
    ) {
        return ResponseEntity.ok(associacaoService.buscarPorProjetoDestino(projetoId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir associação", description = "Remove uma associação existente")
    public ResponseEntity<Void> excluir(
            @PathVariable String id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        associacaoService.excluir(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
} 