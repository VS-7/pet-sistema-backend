package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.notificacao.NotificacaoRequest;
import com.ifcolab.pet_sistema_backend.dto.notificacao.NotificacaoResponse;
import com.ifcolab.pet_sistema_backend.service.NotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificacoes")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Endpoints para gerenciamento de notificações")
@SecurityRequirement(name = "bearerAuth")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    @Operation(summary = "Enviar notificação", description = "Envia uma notificação para um ou mais usuários")
    public ResponseEntity<List<NotificacaoResponse>> enviar(
            @RequestBody @Valid NotificacaoRequest request
    ) {
        return ResponseEntity.ok(notificacaoService.enviar(request));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar notificações", description = "Lista as notificações de um usuário")
    public ResponseEntity<Page<NotificacaoResponse>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "false") boolean apenasNaoLidas,
            Pageable pageable
    ) {
        return ResponseEntity.ok(notificacaoService.listarPorUsuario(usuarioId, apenasNaoLidas, pageable));
    }

    @PatchMapping("/{id}/lida")
    @Operation(summary = "Marcar como lida", description = "Marca uma notificação como lida")
    public ResponseEntity<NotificacaoResponse> marcarComoLida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.marcarComoLida(id));
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas/contagem")
    @Operation(summary = "Contar não lidas", description = "Retorna o número de notificações não lidas de um usuário")
    public ResponseEntity<Long> contarNaoLidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(notificacaoService.contarNaoLidas(usuarioId));
    }
} 