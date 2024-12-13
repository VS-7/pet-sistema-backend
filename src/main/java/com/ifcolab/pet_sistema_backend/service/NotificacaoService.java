package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.dto.notificacao.NotificacaoRequest;
import com.ifcolab.pet_sistema_backend.dto.notificacao.NotificacaoResponse;
import com.ifcolab.pet_sistema_backend.exception.NotificacaoNaoEncontradaException;
import com.ifcolab.pet_sistema_backend.exception.UsuarioNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.model.notificacao.Notificacao;
import com.ifcolab.pet_sistema_backend.repository.NotificacaoRepository;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    @Transactional
    public List<NotificacaoResponse> enviar(NotificacaoRequest request) {
        var usuarios = usuarioRepository.findAllById(request.getUsuariosIds());
        if (usuarios.isEmpty()) {
            throw new UsuarioNaoEncontradoException("Nenhum usuário encontrado com os IDs fornecidos");
        }

        return usuarios.stream()
                .map(usuario -> {
                    var notificacao = Notificacao.builder()
                            .usuario(usuario)
                            .titulo(request.getTitulo())
                            .mensagem(request.getMensagem())
                            .tipo(request.getTipo())
                            .build();

                    var notificacaoSalva = notificacaoRepository.save(notificacao);

                    if (request.isEnviarEmail()) {
                        Map<String, Object> variaveis = new HashMap<>();
                        variaveis.put("titulo", request.getTitulo());
                        variaveis.put("mensagem", request.getMensagem());
                        variaveis.put("nomeUsuario", usuario.getNome());

                        emailService.enviarEmail(
                                usuario.getEmail(),
                                request.getTitulo(),
                                "notificacao",
                                variaveis
                        );
                    }

                    return converterParaResponse(notificacaoSalva);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<NotificacaoResponse> listarPorUsuario(Long usuarioId, boolean apenasNaoLidas, Pageable pageable) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

        Page<Notificacao> notificacoes = apenasNaoLidas ?
                notificacaoRepository.findByUsuarioAndLidaOrderByDataEnvioDesc(usuario, false, pageable) :
                notificacaoRepository.findByUsuarioOrderByDataEnvioDesc(usuario, pageable);

        return notificacoes.map(this::converterParaResponse);
    }

    @Transactional
    public NotificacaoResponse marcarComoLida(Long id) {
        var notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new NotificacaoNaoEncontradaException(id));

        notificacao.setLida(true);
        notificacao.setDataLeitura(LocalDateTime.now());

        return converterParaResponse(notificacaoRepository.save(notificacao));
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
        
        return notificacaoRepository.countByUsuarioAndLida(usuario, false);
    }

    private NotificacaoResponse converterParaResponse(Notificacao notificacao) {
        return NotificacaoResponse.builder()
                .id(notificacao.getId())
                .usuarioId(notificacao.getUsuario().getId())
                .titulo(notificacao.getTitulo())
                .mensagem(notificacao.getMensagem())
                .tipo(notificacao.getTipo())
                .lida(notificacao.isLida())
                .dataEnvio(notificacao.getDataEnvio())
                .dataLeitura(notificacao.getDataLeitura())
                .build();
    }
} 