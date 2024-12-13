package com.ifcolab.pet_sistema_backend.exception;

public class DocumentoNaoEncontradoException extends RuntimeException {
    public DocumentoNaoEncontradoException(Long id) {
        super("Documento não encontrado com ID: " + id);
    }
} 