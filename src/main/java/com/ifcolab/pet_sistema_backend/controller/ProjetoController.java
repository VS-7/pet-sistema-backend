package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoRequest;
import com.ifcolab.pet_sistema_backend.dto.projeto.ProjetoResponse;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.service.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/projetos")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "Endpoints para gerenciamento de projetos")
@SecurityRequirement(name = "bearerAuth")
public class ProjetoController {

    private final ProjetoService projetoService;

    @PostMapping
    @Operation(summary = "Criar projeto", description = "Cria um novo projeto")
    public ResponseEntity<ProjetoResponse> criar(
            @RequestBody @Valid ProjetoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var projeto = projetoService.criar(request, usuarioLogado);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(projeto.getId())
                .toUri();
        return ResponseEntity.created(uri).body(projeto);
    }

    @GetMapping
    @Operation(summary = "Listar projetos", description = "Lista todos os projetos com paginação")
    public ResponseEntity<Page<ProjetoResponse>> listar(Pageable pageable) {
        return ResponseEntity.ok(projetoService.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto", description = "Busca um projeto pelo ID")
    public ResponseEntity<ProjetoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto", description = "Atualiza um projeto existente")
    public ResponseEntity<ProjetoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProjetoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(projetoService.atualizar(id, request, usuarioLogado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto", description = "Exclui um projeto existente")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        projetoService.excluir(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
} 