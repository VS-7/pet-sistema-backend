package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.documento.DocumentoRequest;
import com.ifcolab.pet_sistema_backend.dto.documento.DocumentoResponse;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.service.DocumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos", description = "Endpoints para gerenciamento de documentos")
@SecurityRequirement(name = "bearerAuth")
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping
    @Operation(summary = "Criar documento", description = "Cria um novo documento")
    public ResponseEntity<DocumentoResponse> criar(
            @RequestBody @Valid DocumentoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var documento = documentoService.criar(request, usuarioLogado);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(documento.getId())
                .toUri();
        return ResponseEntity.created(uri).body(documento);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar documento", description = "Busca um documento pelo ID")
    public ResponseEntity<DocumentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar documento", description = "Atualiza um documento existente")
    public ResponseEntity<DocumentoResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DocumentoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(documentoService.atualizar(id, request, usuarioLogado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir documento", description = "Exclui um documento existente")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        documentoService.excluir(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
} 