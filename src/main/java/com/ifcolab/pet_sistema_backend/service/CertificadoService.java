package com.ifcolab.pet_sistema_backend.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ifcolab.pet_sistema_backend.dto.certificado.CertificadoRequest;
import com.ifcolab.pet_sistema_backend.dto.certificado.CertificadoResponse;
import com.ifcolab.pet_sistema_backend.exception.CertificadoJaExisteException;
import com.ifcolab.pet_sistema_backend.exception.CertificadoNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.exception.ProjetoNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.exception.UsuarioNaoEncontradoException;
import com.ifcolab.pet_sistema_backend.model.certificado.Certificado;
import com.ifcolab.pet_sistema_backend.repository.CertificadoRepository;
import com.ifcolab.pet_sistema_backend.repository.ProjetoRepository;
import com.ifcolab.pet_sistema_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificadoService {

    private final CertificadoRepository certificadoRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${app.certificado.url-validacao}")
    private String urlValidacao;

    @Transactional
    public CertificadoResponse emitir(CertificadoRequest request) {
        var projeto = projetoRepository.findById(request.getProjetoId())
                .orElseThrow(() -> new ProjetoNaoEncontradoException(request.getProjetoId()));

        var usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new UsuarioNaoEncontradoException(request.getUsuarioId()));

        if (certificadoRepository.existsByProjetoAndUsuario(projeto, usuario)) {
            throw new CertificadoJaExisteException(projeto.getId(), usuario.getId());
        }

        var codigo = gerarCodigoCertificado();
        var qrCode = gerarQRCode(codigo);

        var certificado = Certificado.builder()
                .projeto(projeto)
                .usuario(usuario)
                .codigo(codigo)
                .qrCode(qrCode)
                .dataInicioProjeto(request.getDataInicioProjeto())
                .dataFimProjeto(request.getDataFimProjeto())
                .descricaoAtividades(request.getDescricaoAtividades())
                .cargaHoraria(request.getCargaHoraria())
                .build();

        var certificadoSalvo = certificadoRepository.save(certificado);
        return converterParaResponse(certificadoSalvo);
    }

    @Transactional(readOnly = true)
    public CertificadoResponse buscarPorId(Long id) {
        return certificadoRepository.findById(id)
                .map(this::converterParaResponse)
                .orElseThrow(() -> new CertificadoNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public CertificadoResponse validar(String codigo) {
        return certificadoRepository.findByCodigo(codigo)
                .map(this::converterParaResponse)
                .orElseThrow(() -> new CertificadoNaoEncontradoException("Código inválido: " + codigo));
    }

    @Transactional(readOnly = true)
    public Page<CertificadoResponse> listarPorUsuario(Long usuarioId, Pageable pageable) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));
        
        return certificadoRepository.findByUsuario(usuario, pageable)
                .map(this::converterParaResponse);
    }

    @Transactional(readOnly = true)
    public Certificado buscarEntidade(Long id) {
        return certificadoRepository.findById(id)
                .orElseThrow(() -> new CertificadoNaoEncontradoException(id));
    }

    private String gerarCodigoCertificado() {
        return UUID.randomUUID().toString();
    }

    private String gerarQRCode(String codigo) {
        try {
            var qrCodeWriter = new QRCodeWriter();
            var url = urlValidacao + codigo;
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);

            var outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar QR Code", e);
        }
    }

    private CertificadoResponse converterParaResponse(Certificado certificado) {
        return CertificadoResponse.builder()
                .id(certificado.getId())
                .projetoId(certificado.getProjeto().getId())
                .projetoTitulo(certificado.getProjeto().getTitulo())
                .usuarioId(certificado.getUsuario().getId())
                .usuarioNome(certificado.getUsuario().getNome())
                .codigo(certificado.getCodigo())
                .qrCode(certificado.getQrCode())
                .dataEmissao(certificado.getDataEmissao())
                .dataInicioProjeto(certificado.getDataInicioProjeto())
                .dataFimProjeto(certificado.getDataFimProjeto())
                .descricaoAtividades(certificado.getDescricaoAtividades())
                .cargaHoraria(certificado.getCargaHoraria())
                .build();
    }
} 