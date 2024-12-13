package com.ifcolab.pet_sistema_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifcolab.pet_sistema_backend.model.log.LogAtividade;
import com.ifcolab.pet_sistema_backend.model.log.TipoAcao;
import com.ifcolab.pet_sistema_backend.model.usuario.Usuario;
import com.ifcolab.pet_sistema_backend.repository.LogAtividadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogAtividadeService {

    private final LogAtividadeRepository logAtividadeRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void registrar(Usuario usuario, String entidade, Long entidadeId, TipoAcao acao, Object detalhes) {
        try {
            JsonNode detalhesJson = detalhes != null ? objectMapper.valueToTree(detalhes) : null;
            
            var log = LogAtividade.builder()
                    .usuario(usuario)
                    .entidade(entidade)
                    .entidadeId(entidadeId)
                    .acao(acao)
                    .detalhes(detalhesJson)
                    .build();
                    
            logAtividadeRepository.save(log);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao registrar log de atividade", e);
        }
    }
} 