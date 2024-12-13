package com.ifcolab.pet_sistema_backend.model.certificado;

import com.ifcolab.pet_sistema_backend.model.projeto.Projeto;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificados")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Certificado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private String codigo;
    
    @Column(nullable = false)
    private String qrCode;
    
    @Column(nullable = false)
    private LocalDateTime dataEmissao;
    
    @Column(nullable = false)
    private LocalDateTime dataInicioProjeto;
    
    @Column(nullable = false)
    private LocalDateTime dataFimProjeto;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricaoAtividades;
    
    @Column(nullable = false)
    private Integer cargaHoraria;
    
    @PrePersist
    protected void onCreate() {
        dataEmissao = LocalDateTime.now();
    }
} 