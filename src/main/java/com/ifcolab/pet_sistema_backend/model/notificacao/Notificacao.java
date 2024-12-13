package com.ifcolab.pet_sistema_backend.model.notificacao;

import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;
    
    @Column(nullable = false)
    private boolean lida;
    
    @Column(nullable = false)
    private LocalDateTime dataEnvio;
    
    private LocalDateTime dataLeitura;
    
    @PrePersist
    protected void onCreate() {
        dataEnvio = LocalDateTime.now();
        lida = false;
    }
} 