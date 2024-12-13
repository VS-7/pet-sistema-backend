package com.ifcolab.pet_sistema_backend.dto.usuario;

import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long id;
    private String nome;
    private String email;
    private TipoUsuario tipo;
} 