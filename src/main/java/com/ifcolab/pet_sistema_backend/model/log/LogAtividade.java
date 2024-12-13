package com.ifcolab.pet_sistema_backend.model.log;

import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "logs_atividades")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogAtividade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private String entidade;
    
    @Column(nullable = false)
    private Long entidadeId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAcao acao;
    
    @Column(columnDefinition = "jsonb")
    private String detalhes;
    
    @Column(nullable = false)
    private LocalDateTime dataAcao;
    
    @PrePersist
    protected void onCreate() {
        dataAcao = LocalDateTime.now();
    }
} 