package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.log.LogAtividade;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogAtividadeRepository extends JpaRepository<LogAtividade, Long> {
    Page<LogAtividade> findByUsuario(Usuario usuario, Pageable pageable);
    
    Page<LogAtividade> findByEntidadeAndEntidadeId(String entidade, Long entidadeId, Pageable pageable);
} 