package com.ifcolab.pet_sistema_backend.model.log;

import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import org.hibernate.annotations.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;

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
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode detalhes;
    
    @Column(nullable = false)
    private LocalDateTime dataAcao;
    
    @PrePersist
    protected void onCreate() {
        dataAcao = LocalDateTime.now();
    }
} 