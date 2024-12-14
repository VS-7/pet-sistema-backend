package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.certificado.CertificadoRequest;
import com.ifcolab.pet_sistema_backend.dto.certificado.CertificadoResponse;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.service.CertificadoService;
import com.ifcolab.pet_sistema_backend.service.GeradorPdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/certificados")
@RequiredArgsConstructor
@Tag(name = "Certificados", description = "Endpoints para gerenciamento de certificados")
@SecurityRequirement(name = "bearerAuth")
public class CertificadoController {

    private final CertificadoService certificadoService;
    private final GeradorPdfService geradorPdfService;

    @PostMapping
    @PreAuthorize("hasRole('TUTOR')")
    @Operation(summary = "Emitir certificado", description = "Emite um novo certificado para um usuário em um projeto")
    public ResponseEntity<CertificadoResponse> emitir(
            @RequestBody @Valid CertificadoRequest request,
            @AuthenticationPrincipal Usuario usuarioLogado
    ) {
        var certificado = certificadoService.emitir(request);
        var uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(certificado.getId())
                .toUri();
        return ResponseEntity.created(uri).body(certificado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar certificado", description = "Busca um certificado pelo ID")
    public ResponseEntity<CertificadoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(certificadoService.buscarPorId(id));
    }

    @GetMapping("/validar/{codigo}")
    @Operation(summary = "Validar certificado", description = "Valida um certificado pelo código")
    public ResponseEntity<CertificadoResponse> validar(@PathVariable String codigo) {
        return ResponseEntity.ok(certificadoService.validar(codigo));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar certificados", description = "Lista todos os certificados de um usuário")
    public ResponseEntity<Page<CertificadoResponse>> listarPorUsuario(
            @PathVariable Long usuarioId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(certificadoService.listarPorUsuario(usuarioId, pageable));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Baixar certificado", description = "Faz o download do certificado em PDF")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable Long id) {
        var certificado = certificadoService.buscarEntidade(id);
        byte[] pdf = geradorPdfService.gerarCertificadoPdf(certificado);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificado-" + id + ".pdf\"")
                .body(pdf);
    }
} 