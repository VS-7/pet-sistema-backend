package com.ifcolab.pet_sistema_backend.exception;

public class CertificadoJaExisteException extends RuntimeException {
    public CertificadoJaExisteException(Long projetoId, Long usuarioId) {
        super("Já existe um certificado emitido para o projeto " + projetoId + " e usuário " + usuarioId);
    }
} 