package com.ifcolab.pet_sistema_backend.exception;

public class AssociacaoNaoEncontradaException extends RuntimeException {
    public AssociacaoNaoEncontradaException(String id) {
        super("Associação não encontrada com ID: " + id);
    }
} 