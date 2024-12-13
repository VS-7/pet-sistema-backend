package com.ifcolab.pet_sistema_backend.dto.notificacao;

import com.ifcolab.pet_sistema_backend.model.notificacao.TipoNotificacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoResponse {
    private Long id;
    private Long usuarioId;
    private String titulo;
    private String mensagem;
    private TipoNotificacao tipo;
    private boolean lida;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataLeitura;
} 