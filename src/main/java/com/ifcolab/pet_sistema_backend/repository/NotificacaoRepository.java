package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.notificacao.Notificacao;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    Page<Notificacao> findByUsuarioOrderByDataEnvioDesc(Usuario usuario, Pageable pageable);
    Page<Notificacao> findByUsuarioAndLidaOrderByDataEnvioDesc(Usuario usuario, boolean lida, Pageable pageable);
    long countByUsuarioAndLida(Usuario usuario, boolean lida);
} 