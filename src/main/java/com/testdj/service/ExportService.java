package com.testdj.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {

    public byte[] exportTabResult(String tab, Map<String, Object> resultData) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        String title = getTabTitle(tab);
        document.add(new Paragraph(title).setFontSize(20).setBold());
        document.add(new Paragraph("Export Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        document.add(new Paragraph(" "));

        // Render result data as a table
        if (resultData != null && !resultData.isEmpty()) {
            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            table.addHeaderCell(new Cell().add(new Paragraph("Field").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Value").setBold()));

            for (Map.Entry<String, Object> entry : resultData.entrySet()) {
                table.addCell(new Cell().add(new Paragraph(entry.getKey())));
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                table.addCell(new Cell().add(new Paragraph(value)));
            }
            document.add(table);
        } else {
            document.add(new Paragraph("No data available for this tab."));
        }

        document.close();
        return baos.toByteArray();
    }

    private String getTabTitle(String tab) {
        return switch (tab) {
            case "hello" -> "Hello World - Result Export";
            case "hash" -> "SHA-256 Hash - Result Export";
            case "sort" -> "Bubble Sort - Result Export";
            default -> "Unknown Tab - Result Export";
        };
    }
}