package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.auth.AuthenticationResponse;
import com.ifcolab.pet_sistema_backend.dto.auth.LoginRequest;
import com.ifcolab.pet_sistema_backend.dto.auth.RegisterRequest;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import com.ifcolab.pet_sistema_backend.exception.EmailJaCadastradoException;
import com.ifcolab.pet_sistema_backend.exception.UsuarioNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import com.ifcolab.pet_sistema_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${spring.security.jwt.expiration}")
    private long jwtExpiration;

    @Transactional
    public AuthenticationResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }

        var usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .tipo(request.getTipo())
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        var usuarioSalvo = usuarioRepository.save(usuario);
        
        return gerarTokenResponse(usuarioSalvo);
    }

    public AuthenticationResponse autenticar(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(request.getEmail()));

        return gerarTokenResponse(usuario);
    }
    
    public UsuarioResponse getUsuarioAutenticado(String email) {
        var usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(email));

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .tipo(usuario.getTipo())
                .build();
    }

    private AuthenticationResponse gerarTokenResponse(Usuario usuario) {
        String token = jwtService.generateToken(usuario);
        
        return AuthenticationResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration / 1000)
                .tipo(usuario.getTipo())
                .build();
    }
} 