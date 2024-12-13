package com.ifcolab.pet_sistema_backend.exception;

public class CertificadoNaoEncontradoException extends RuntimeException {
    public CertificadoNaoEncontradoException(Long id) {
        super("Certificado não encontrado com ID: " + id);
    }

    public CertificadoNaoEncontradoException(String message) {
        super(message);
    }
} 