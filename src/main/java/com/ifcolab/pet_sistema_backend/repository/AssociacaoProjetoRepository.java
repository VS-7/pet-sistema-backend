package com.ifcolab.pet_sistema_backend.repository;

import com.ifcolab.pet_sistema_backend.model.associacao.AssociacaoProjeto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociacaoProjetoRepository extends MongoRepository<AssociacaoProjeto, String> {
    List<AssociacaoProjeto> findByProjetoOrigemId(Long projetoOrigemId);
    
    List<AssociacaoProjeto> findByProjetoDestinoId(Long projetoDestinoId);
    
    void deleteByProjetoOrigemIdOrProjetoDestinoId(Long projetoOrigemId, Long projetoDestinoId);
} 