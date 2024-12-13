package com.ifcolab.pet_sistema_backend.exception;

public class TituloDocumentoDuplicadoException extends RuntimeException {
    public TituloDocumentoDuplicadoException(String titulo) {
        super("Já existe um documento com o título: " + titulo);
    }
} 