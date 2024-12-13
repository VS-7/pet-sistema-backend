package com.ifcolab.pet_sistema_backend.service;

import com.ifcolab.pet_sistema_backend.model.certificado.Certificado;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class GeradorPdfService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] gerarCertificadoPdf(Certificado certificado) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4.rotate());

            // Configurar fontes
            PdfFont fontTitulo = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Título
            Paragraph titulo = new Paragraph("CERTIFICADO")
                    .setFont(fontTitulo)
                    .setFontSize(24)
                    .setMarginTop(50);
            document.add(titulo);

            // Conteúdo
            document.add(new Paragraph("\n\n"));
            document.add(new Paragraph("Certificamos que")
                    .setFont(fontNormal)
                    .setFontSize(14));

            document.add(new Paragraph("\n\n"));

            document.add(new Paragraph(certificado.getUsuario().getNome())
                    .setFont(fontTitulo)
                    .setFontSize(18));

            String texto = String.format(
                    "participou do projeto \"%s\" no período de %s a %s, " +
                    "com carga horária total de %d horas, desenvolvendo as seguintes atividades:",
                    certificado.getProjeto().getTitulo(),
                    certificado.getDataInicioProjeto().format(FORMATTER),
                    certificado.getDataFimProjeto().format(FORMATTER),
                    certificado.getCargaHoraria()
            );

            document.add(new Paragraph(texto)
                    .setFont(fontNormal)
                    .setFontSize(14)
                    .setMarginTop(20));

            document.add(new Paragraph(certificado.getDescricaoAtividades())
                    .setFont(fontNormal)
                    .setFontSize(12)
                    .setMarginTop(20)
                    .setMarginLeft(50)
                    .setMarginRight(50));

            // Adicionar QR Code
            byte[] qrCodeBytes = Base64.getDecoder().decode(certificado.getQrCode());
            Image qrCode = new Image(com.itextpdf.io.image.ImageDataFactory.create(qrCodeBytes))
                    .setWidth(100)
                    .setHeight(100);
            document.add(qrCode);

            // Código de validação
            document.add(new Paragraph("Código de validação: " + certificado.getCodigo())
                    .setFont(fontNormal)
                    .setFontSize(10));

            // Data de emissão
            document.add(new Paragraph("Emitido em: " + certificado.getDataEmissao().format(FORMATTER))
                    .setFont(fontNormal)
                    .setFontSize(10)
                    .setMarginRight(50)
                    .setMarginTop(50));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF do certificado", e);
        }
    }
} 