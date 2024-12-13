package com.ifcolab.pet_sistema_backend.integration;

import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
public class TestUsuario extends Usuario {
    private String token;

    public TestUsuario() {
        super();
    }

    public TestUsuario(Long id, String nome, String email, String senha, TipoUsuario tipo, String token) {
        super(id, nome, email, senha, tipo, LocalDateTime.now(), LocalDateTime.now());
        this.token = token;
    }
} 