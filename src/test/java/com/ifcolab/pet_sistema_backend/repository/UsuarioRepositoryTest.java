package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.usuario.TipoUsuario;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void findByEmail_DeveRetornarUsuario_QuandoEmailExiste() {
        // Arrange
        var usuario = Usuario.builder()
                .nome("Test User")
                .email("test@example.com")
                .senha("password123")
                .tipo(TipoUsuario.PETIANO)
                .build();
        
        entityManager.persist(usuario);
        entityManager.flush();

        // Act
        var found = usuarioRepository.findByEmail("test@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_DeveRetornarVazio_QuandoEmailNaoExiste() {
        // Act
        var found = usuarioRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_DeveRetornarTrue_QuandoEmailExiste() {
        // Arrange
        var usuario = Usuario.builder()
                .nome("Test User")
                .email("test@example.com")
                .senha("password123")
                .tipo(TipoUsuario.PETIANO)
                .build();
        
        entityManager.persist(usuario);
        entityManager.flush();

        // Act
        boolean exists = usuarioRepository.existsByEmail("test@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_DeveRetornarFalse_QuandoEmailNaoExiste() {
        // Act
        boolean exists = usuarioRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(exists).isFalse();
    }
} 