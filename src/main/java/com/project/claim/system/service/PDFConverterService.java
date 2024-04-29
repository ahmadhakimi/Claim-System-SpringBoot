package com.project.claim.system.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class PDFConverterService {

    public void export (HttpServletResponse response) {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());

        doc.open();

        Font fontTitle = FontFactory.getFont(FontFactory.COURIER_BOLD);
        fontTitle.setSize(20);

        Paragraph paragraph = new Paragraph("Claims: " , fontTitle);
        paragraph.setAlignment(paragraph.ALIGN_CENTER);

        Font fontParagraph = FontFactory.getFont(FontFactory.COURIER);
        fontParagraph.setSize(14);

        Paragraph subParagraph = new Paragraph("details: ", fontParagraph);
        subParagraph.setAlignment(paragraph.ALIGN_LEFT);

        doc.add(paragraph);
        doc.add(subParagraph);
        doc.close();
    }
}
