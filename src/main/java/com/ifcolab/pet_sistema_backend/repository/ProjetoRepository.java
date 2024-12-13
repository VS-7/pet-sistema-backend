package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.projeto.Projeto;
import com.ifcolab.pet_sistema_backend.model.projeto.StatusProjeto;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    Page<Projeto> findByTutor(Usuario tutor, Pageable pageable);
    
    Page<Projeto> findByParticipantesContaining(Usuario participante, Pageable pageable);
    
    Page<Projeto> findByStatus(StatusProjeto status, Pageable pageable);
    
    @Query("SELECT p FROM Projeto p WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))")
    Page<Projeto> buscarPorTermo(String termo, Pageable pageable);
    
    List<Projeto> findByTutorAndStatus(Usuario tutor, StatusProjeto status);
} 