package com.ifcolab.pet_sistema_backend.controller;

import com.ifcolab.pet_sistema_backend.dto.auth.AuthenticationResponse;
import com.ifcolab.pet_sistema_backend.dto.auth.LoginRequest;
import com.ifcolab.pet_sistema_backend.dto.auth.RegisterRequest;
import com.ifcolab.pet_sistema_backend.service.AuthenticationService;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/registrar")
    @Operation(summary = "Registrar novo usuário", description = "Registra um novo usuário no sistema")
    public ResponseEntity<AuthenticationResponse> registrar(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Realizar login", description = "Autentica um usuário existente")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authenticationService.autenticar(request));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter usuário autenticado", description = "Retorna os dados do usuário autenticado")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authenticationService.getUsuarioAutenticado(userDetails.getUsername()));
    }
    
} 