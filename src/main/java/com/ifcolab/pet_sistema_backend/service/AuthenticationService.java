package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.auth.AuthenticationResponse;
import com.ifcolab.pet_sistema_backend.dto.auth.LoginRequest;
import com.ifcolab.pet_sistema_backend.dto.auth.RegisterRequest;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;
import com.ifcolab.pet_sistema_backend.exception.EmailJaCadastradoException;
import com.ifcolab.pet_sistema_backend.exception.UsuarioNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import com.ifcolab.pet_sistema_backend.repository.PetRepository;
import com.ifcolab.pet_sistema_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.security.SecureRandom;

import java.time.LocalDateTime;
import com.ifcolab.pet_sistema_backend.model.pet.Pet;
import com.ifcolab.pet_sistema_backend.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender emailSender;

    @Value("${spring.security.jwt.expiration}")
    private long jwtExpiration;

    @Transactional
    public AuthenticationResponse registrarTutor(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }

        var usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .tipo(TipoUsuario.TUTOR)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        var usuarioSalvo = usuarioRepository.save(usuario);
        return gerarTokenResponse(usuarioSalvo);
    }

    @Transactional
    public AuthenticationResponse registrarPetiano(RegisterRequest request, Usuario tutorLogado) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException(request.getEmail());
        }

        Pet petDoTutor = petRepository.findByTutorId(tutorLogado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor não possui um PET cadastrado"));

        String senhaGerada = gerarSenhaAleatoria();
        
        var usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(senhaGerada))
                .tipo(TipoUsuario.PETIANO)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();

        var usuarioSalvo = usuarioRepository.save(usuario);
        
        petDoTutor.getMembros().add(usuarioSalvo);
        petRepository.save(petDoTutor);
        
        enviarEmailComSenha(request.getEmail(), senhaGerada);
        
        return gerarTokenResponse(usuarioSalvo);
    }

    private String gerarSenhaAleatoria() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            senha.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return senha.toString();
    }

    private void enviarEmailComSenha(String emailDestino, String senha) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestino);
        message.setSubject("Suas credenciais de acesso ao Sistema PET");
        message.setText("Olá!\n\nSua conta foi criada no Sistema PET. Use as seguintes credenciais para acessar:\n\n" +
                "Email: " + emailDestino + "\n" +
                "Senha: " + senha + "\n\n" +
                "Recomendamos que você altere sua senha após o primeiro acesso.\n\n" +
                "Atenciosamente,\nEquipe PET");
        
        emailSender.send(message);
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