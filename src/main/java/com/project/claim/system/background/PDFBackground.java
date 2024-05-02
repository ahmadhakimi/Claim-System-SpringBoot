package com.project.claim.system.background;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import java.nio.file.Paths;

public class PDFBackground {
    public static void main(String[] args) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Load the background image
            try (InputStream imageStream = PDFBackground.class.getResourceAsStream("/rbtsb-logo.jpg")) {
                // Convert the image stream to byte array
                byte[] imageBytes = readAllBytes(imageStream);

                PDImageXObject backgroundImage = PDImageXObject.createFromByteArray(document, imageBytes, "rbtsb-logo.jpg");

                // Iterate over each page in the document
                for (PDPage currentPage : document.getPages()) {
                    try (PDPageContentStream contentStream = new PDPageContentStream(document, currentPage, PDPageContentStream.AppendMode.PREPEND, true, true)) {
                        contentStream.drawImage(backgroundImage, 0, 0, currentPage.getMediaBox().getWidth(), currentPage.getMediaBox().getHeight());
                    }
                }

                // Save the modified document
                try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                    document.save(outputStream);
                    document.close();
                    // Save the PDF to a file or return it as needed
                    Files.write(Paths.get("output_document_with_background.pdf"), outputStream.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
