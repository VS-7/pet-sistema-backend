package com.ifcolab.pet_sistema_backend.exception;

public class NotificacaoNaoEncontradaException extends RuntimeException {
    public NotificacaoNaoEncontradaException(Long id) {
        super("Notificação não encontrada com ID: " + id);
    }
} 