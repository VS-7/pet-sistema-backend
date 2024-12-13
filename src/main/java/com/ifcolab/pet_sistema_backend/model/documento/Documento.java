package com.ifcolab.pet_sistema_backend.model.documento;

import com.ifcolab.pet_sistema_backend.model.projeto.Projeto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;

import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Documento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipo;
    
    @Column(nullable = false)
    private String titulo;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode conteudo;
    
    @Column(nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(nullable = false)
    private LocalDateTime dataAtualizacao;
    
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
} 