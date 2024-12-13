package com.ifcolab.pet_sistema_backend.dto.notificacao;

import com.ifcolab.pet_sistema_backend.model.notificacao.TipoNotificacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoRequest {
    
    @NotNull(message = "Os IDs dos usuários são obrigatórios")
    private Set<Long> usuariosIds;
    
    @NotBlank(message = "O título é obrigatório")
    private String titulo;
    
    @NotBlank(message = "A mensagem é obrigatória")
    private String mensagem;
    
    @NotNull(message = "O tipo de notificação é obrigatório")
    private TipoNotificacao tipo;
    
    private boolean enviarEmail;
} 