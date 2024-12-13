package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.documento.Documento;
import com.ifcolab.pet_sistema_backend.model.documento.TipoDocumento;
import com.ifcolab.pet_sistema_backend.model.projeto.Projeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByProjeto(Projeto projeto);
    
    Page<Documento> findByProjetoAndTipo(Projeto projeto, TipoDocumento tipo, Pageable pageable);
    
    boolean existsByProjetoAndTitulo(Projeto projeto, String titulo);
    
    long countByProjetoIn(Collection<Projeto> projetos);
} 