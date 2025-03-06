package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.pet.PetRequest;
import com.ifcolab.pet_sistema_backend.dto.pet.PetResponse;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "PET", description = "Endpoints para gerenciamento de grupos PET")
@SecurityRequirement(name = "bearerAuth")
public class PetController {

    private final PetService petService;

    @PostMapping
    @PreAuthorize("hasRole('TUTOR')")
    @Operation(summary = "Criar PET", description = "Cria um novo grupo PET")
    public ResponseEntity<PetResponse> criar(
            @RequestBody @Valid PetRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var pet = petService.criar(request, usuarioLogado);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pet.getId())
                .toUri();
        return ResponseEntity.created(uri).body(pet);
    }

    @GetMapping
    @Operation(summary = "Listar PETs", description = "Lista todos os PETs que o usuário tem acesso")
    public ResponseEntity<List<PetResponse>> listar(
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(petService.listarPorUsuario(usuarioLogado));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar PET", description = "Busca um PET pelo ID")
    public ResponseEntity<PetResponse> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(petService.buscarPorId(id, usuarioLogado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar PET", description = "Atualiza um PET existente")
    public ResponseEntity<PetResponse> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PetRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        return ResponseEntity.ok(petService.atualizar(id, request, usuarioLogado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TUTOR')")
    @Operation(summary = "Excluir PET", description = "Exclui um PET existente")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        petService.excluir(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
} 