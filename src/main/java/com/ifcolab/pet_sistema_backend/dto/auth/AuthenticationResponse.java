package com.ifcolab.pet_sistema_backend.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.dto.usuario.UsuarioResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("token_type")
    private String tokenType;
    
    @JsonProperty("expires_in")
    private long expiresIn;

    private TipoUsuario tipo;

    private UsuarioResponse usuario;
} 