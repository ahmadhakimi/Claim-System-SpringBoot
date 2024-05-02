package com.project.claim.system.service;

import com.project.claim.system.dto.ClaimDTO;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PDFGeneratorService {

    public void drawTableHeader(PDPageContentStream contentStream, float y, float[] cellWidths) throws IOException {
        String[] headers = {"Claim ID", "Name", "Desc.", "Amount (RM)", "Rcpt. No.", "Rcpt. Date", "Status", "Staff ID", "Full Name", "Attc. ID", "Attc. Name"};
        float x = 0f;
        for (int i = 0; i < headers.length; i++) {
            drawCell(contentStream, headers[i], x, y, cellWidths[i], 20f, true); // Pass true to indicate bold font
            x += cellWidths[i];
        }
    }


    public void drawTableRows(PDPageContentStream contentStream, List<ClaimDTO> claims, float startY, float[] cellWidths, float lineHeight) throws IOException {
        for (ClaimDTO claim : claims) {
            float rowHeight = calculateRowHeight(claim, cellWidths, lineHeight);
            float x = 0f;
            for (int i = 0; i < 11; i++) {
                drawCell(contentStream, getCellValue(claim, i, cellWidths[i]), x, startY, cellWidths[i], rowHeight, false);
                x += cellWidths[i];
            }
            startY -= rowHeight;
        }
    }

    public float calculateRowHeight(ClaimDTO claim, float[] cellWidths, float lineHeight) throws IOException {
        float maxHeight = 0;
        for (int i = 0; i < 11; i++) {
            List<String> lines = splitTextIntoLines(getCellValue(claim, i, cellWidths[i]), cellWidths[i]);
            float cellHeight = (lines.size() * lineHeight) - 24f;
            if (cellHeight > maxHeight) {
                maxHeight = cellHeight;
            }
        }
        return maxHeight;
    }


    public void drawCell(PDPageContentStream contentStream, String text, float x, float y, float width, float height, boolean bold) throws IOException {
        // Draw cell border
        contentStream.setStrokingColor(Color.BLACK);
        contentStream.setLineWidth(1f);
        contentStream.addRect(x, y - height, width, height);
        contentStream.stroke();

        // Split text into lines
        List<String> lines = splitTextIntoLines(text, width - 4);
        float lineHeight = 12;

        // Determine the maximum number of lines that can fit in the cell height
        int maxLines = (int) Math.floor(height / lineHeight);

        if (lines.size() > maxLines) {
            // Trim the lines to fit the maximum allowed lines
            lines = lines.subList(0, maxLines);
            // Adjust the height to fit the trimmed lines
            height = maxLines * lineHeight + 4;
        }

        // Set font and draw text
        PDFont font = bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA; // Use bold font if specified
        contentStream.setFont(font, 9);
        contentStream.setLeading(lineHeight);
        contentStream.beginText();
        contentStream.newLineAtOffset(x + 2, y - 12); // Adjust offset for text alignment
        for (String line : lines) {
            contentStream.showText(line);
            contentStream.newLineAtOffset(0, -lineHeight);
        }
        contentStream.endText();
    }



    public String getCellValue(ClaimDTO claim, int columnIndex, float cellWidth) throws IOException {
        switch (columnIndex) {
            case 0:
                return wrapText(claim.getId().toString(), cellWidth / 70f); // Adjust the divisor as needed
            case 1:
                return wrapTextName(String.valueOf(claim.getName()), 8);
            case 2:
                return claim.getDescription();
            case 3:
                return String.valueOf(claim.getAmount());
            case 4:
                return claim.getReceiptNo();
            case 5:
                return formatDate(claim.getReceiptDate());
            case 6:
                return claim.getStatus().toString();
            case 7:
                return wrapText(claim.getStaffId().toString(), cellWidth / 70f);
            case 8:
                return claim.getFullName();
            case 9:
                return wrapText(claim.getAttachmentId().toString(), cellWidth /70f);
            case 10:
                return claim.getAttachmentName();
            default:
                return "";
        }
    }


    public String wrapText(String text, float maxWidth) {
        // Split the text into substrings of length 4 to fit within the cell width
        List<String> lines = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += 10) {
            int endIndex = Math.min(i + 10, length);
            lines.add(text.substring(i, endIndex));
        }
        return String.join("\n", lines);
    }

    public String wrapTextName(String text, int maxCharsPerLine) {
        StringBuilder wrappedText = new StringBuilder();
        for (int i = 0; i < text.length(); i += maxCharsPerLine) {
            int endIndex = Math.min(i + maxCharsPerLine, text.length());
            wrappedText.append(text, i, endIndex).append("\n");
        }
        return wrappedText.toString();
    }

    public List<String> splitTextIntoLines(String text, float maxWidth) throws IOException {
        List<String> lines = new ArrayList<>();
        try (StringReader reader = new StringReader(text)) {
            try (BufferedReader br = new BufferedReader(reader)) {
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    for (String word : line.split("\\s")) {
                        if (word.length() > maxWidth) {
                            // Word exceeds maximum width, split it
                            int endIndex = 0;
                            while (endIndex < word.length()) {
                                int startIndex = endIndex;
                                endIndex = Math.min(startIndex + (int) maxWidth, word.length());
                                lines.add(word.substring(startIndex, endIndex));
                            }
                        } else {
                            // Word fits within maximum width
                            if (PDType1Font.HELVETICA.getStringWidth(builder.toString() + word) / 1000 * 10 > maxWidth) {
                                lines.add(builder.toString().trim());
                                builder.setLength(0); // Clear the StringBuilder
                            }
                            builder.append(word).append(" ");
                        }
                    }
                    lines.add(builder.toString().trim());
                    builder.setLength(0); // Clear the StringBuilder
                }
            }
        }
        return lines;
    }

    private String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return (date != null) ? dateFormat.format(date) : "";
    }


}
