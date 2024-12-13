package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.certificado.Certificado;
import com.ifcolab.pet_sistema_backend.model.projeto.Projeto;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    Page<Certificado> findByUsuario(Usuario usuario, Pageable pageable);
    Page<Certificado> findByProjeto(Projeto projeto, Pageable pageable);
    Optional<Certificado> findByCodigo(String codigo);
    boolean existsByProjetoAndUsuario(Projeto projeto, Usuario usuario);
    long countByProjetoIn(Collection<Projeto> projetos);
} 