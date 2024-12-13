package com.ifcolab.pet_sistema_backend.exception;

public class ProjetoNaoEncontradoException extends RuntimeException {
    public ProjetoNaoEncontradoException(String message) {
        super(message);
    }
    
    public ProjetoNaoEncontradoException(Long id) {
        super("Projeto não encontrado com ID: " + id);
    }
} 