package com.ifcolab.pet_sistema_backend.service;

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

    @Transactional
    public void registrar(Usuario usuario, String entidade, Long entidadeId, TipoAcao acao, String detalhes) {
        var log = LogAtividade.builder()
                .usuario(usuario)
                .entidade(entidade)
                .entidadeId(entidadeId)
                .acao(acao)
                .detalhes(detalhes)
                .build();
                
        logAtividadeRepository.save(log);
    }
} 