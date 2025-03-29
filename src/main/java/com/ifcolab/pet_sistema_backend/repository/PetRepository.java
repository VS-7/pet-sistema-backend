package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.pet.Pet;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    @Query("SELECT p FROM Pet p WHERE p.tutor = :usuario OR :usuario MEMBER OF p.membros")
    List<Pet> findByUsuario(Usuario usuario);
    
    boolean existsByNome(String nome);
    
    boolean existsByCodigo(String codigo);

    boolean existsByTutorId(Long tutorId);
} 