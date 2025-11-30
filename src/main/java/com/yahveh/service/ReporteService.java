package com.yahveh.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class ReporteService {

    /**
     * Generar reporte en PDF
     */
    public byte[] generarReportePDF(String nombreReporte, Map<String, Object> parametros, List<?> datos) {
        try {
            return generarPDFSimple(nombreReporte, parametros, datos);
        } catch (Exception e) {
            log.error("Error al generar reporte: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar reporte: " + e.getMessage(), e);
        }
    }

    private byte[] generarPDFSimple(String nombreReporte, Map<String, Object> parametros, List<?> datos) {
        try {
            log.info("Generando PDF simple para reporte: {}", nombreReporte);

            // Cargar el archivo JRXML
            InputStream reportStream = getClass().getResourceAsStream("/reportes/" + nombreReporte + ".jrxml");
            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo de reporte: " + nombreReporte + ".jrxml");
            }

            // Compilar el reporte
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // Crear el datasource con los datos
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datos);

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            // Exportar a PDF
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            log.info("PDF simple generado exitosamente");

            return pdfBytes;

        } catch (Exception e) {
            log.error("Error al generar PDF simple: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar PDF simple: " + e.getMessage(), e);
        }
    }

    /**
     * Unir dos PDFs en la misma página (uno arriba, otro abajo)
     */
    private byte[] unirPDFsEnMismaPagina(byte[] pdf1, byte[] pdf2) {
        try (PDDocument doc1 = Loader.loadPDF(pdf1);
             PDDocument doc2 = Loader.loadPDF(pdf2)) {

            PDFMergerUtility pdfMerger = new PDFMergerUtility();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            pdfMerger.appendDocument(doc1, doc2);
            doc1.save(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            log.error("Error al unir PDFs: {}", e.getMessage(), e);
            throw new RuntimeException("Error al unir PDFs: " + e.getMessage(), e);
        }
    }




}