package com.project.claim.system.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.claim.system.dto.AttachmentDTO;
import com.project.claim.system.dto.ClaimDTO;
import com.project.claim.system.entity.AttachmentEntity;
import com.project.claim.system.entity.ClaimEntity;
import com.project.claim.system.enumeration.Status;
import com.project.claim.system.exception.ResourceNotFoundException;
import com.project.claim.system.repository.AttachmentRepository;
import com.project.claim.system.repository.ClaimRepository;
import com.project.claim.system.service.ClaimService;
import com.project.claim.system.service.PDFGeneratorService;
import com.project.claim.system.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDCIDFont;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.awt.*;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


@RestController
@RequestMapping("api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final StaffService staffService;
    private final ClaimService claimService;
    private final AttachmentRepository attachmentRepository;
    private final ClaimRepository claimRepository;
    private final PDFGeneratorService pdfGeneratorService;



    @PostMapping()
    public ResponseEntity<ClaimDTO> createClaimWithAttachment(@RequestParam("claim") String claim, @RequestParam("attachment") MultipartFile attachmentFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        ClaimDTO claimDTO;
        try {
            claimDTO = objectMapper.readValue(claim, ClaimDTO.class);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ClaimDTO createdClaim = claimService.createClaimWithAttachment(claimDTO, attachmentFile); // Pass attachmentFile as well
        return new ResponseEntity<>(createdClaim, HttpStatus.CREATED);
    }


    //GET CLAIM BY PARAMS
    @GetMapping
    public ResponseEntity<List<ClaimDTO>> getClaimsByParams(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) String status) {
        Integer yearValue = parseYear(year);
        Integer monthValue = parseMonth(month);
        UUID staffIdValue = parseStaffId(staffId);
        Status statusValue = parseStatus(status);

        List<ClaimDTO> claims = claimService.getClaimsByParams(yearValue, monthValue, staffIdValue, statusValue);
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }

    //EDIT CLAIMS
    @PutMapping("/{id}")
    public ResponseEntity<ClaimDTO> updateClaimWithAttachment(
            @PathVariable UUID id,
            @RequestParam("claim") String claimJson,
            @RequestParam(value = "attachment", required = false) MultipartFile attachmentFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        ClaimDTO updatedClaimDTO;
        try {
            updatedClaimDTO = objectMapper.readValue(claimJson, ClaimDTO.class);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ClaimDTO updatedClaim = claimService.updateClaimWithAttachment(id, updatedClaimDTO, attachmentFile);
        if (updatedClaim == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(updatedClaim, HttpStatus.OK);
    }


//    DOWNLOAD ATTACHMENT

    @GetMapping("/download/{claimId}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable UUID claimId) {
        // Find the attachment by claimId
        Optional<AttachmentEntity> optionalAttachment = attachmentRepository.findByClaimId(claimId);
        if (optionalAttachment.isPresent()) {
            AttachmentEntity attachmentEntity = optionalAttachment.get();

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(attachmentEntity.getType())); // Set content type dynamically
            headers.setContentDispositionFormData("attachment", attachmentEntity.getName());
            headers.setContentLength(attachmentEntity.getData().length);

            // Return attachment data in response body
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(attachmentEntity.getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    //DELETE CLAIM AND ATTACHMENT
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClaim(@PathVariable UUID id) {
        // Retrieve the claim entity
        Optional<ClaimEntity> optionalClaim = claimRepository.findById(id);
        if (optionalClaim.isPresent()) {
            ClaimEntity claimEntity = optionalClaim.get();

            // Fetch attachmentId from the corresponding ClaimDTO
            UUID attachmentId = claimService.getAttachmentIdByClaimId(id); // Assuming a method exists in claimService to retrieve attachmentId

            // Delete the attachment if it exists
            if (attachmentId != null) {
                attachmentRepository.deleteById(attachmentId);
            }

            // Delete the claim entity
            claimRepository.delete(claimEntity);

            return new ResponseEntity<>("The claim has been deleted successfully.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Claim not found.", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/generate-pdf")
    public ResponseEntity<Resource> generatePDF(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) String status) {
        Integer yearValue = parseYear(year);
        Integer monthValue = parseMonth(month);
        UUID staffIdValue = parseStaffId(staffId);
        Status statusValue = parseStatus(status);

        List<ClaimDTO> claims = claimService.getClaimsByParams(yearValue, monthValue, staffIdValue, statusValue);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
            contentStream.newLineAtOffset(30, 750);
            contentStream.showText("CLAIMS RECORD");
            contentStream.endText();

            float startY = 745f;
            float lineHeight = 20f;

            // Define custom widths for each cell
            float[] cellWidths = {60f, 55f, 50f, 45f, 55f, 58f, 60f, 60f, 60f, 60f, 50f}; // Adjust as needed

            pdfGeneratorService.drawTableHeader(contentStream, startY, cellWidths);
            pdfGeneratorService.drawTableRows(contentStream, claims, startY - lineHeight, cellWidths, lineHeight);

            contentStream.close();

            // Add background image to each page
            PDImageXObject backgroundImage = PDImageXObject.createFromFile("C:/Users/USER/Desktop/SPRINGBOOT/claim system/img/rbtsb-logo.jpg", document);

            float bgWidth = 125;
            float bgHeight = 75;

            for (PDPage currentPage : document.getPages()) {
                PDPageContentStream currentPageContentStream = new PDPageContentStream(document, currentPage, PDPageContentStream.AppendMode.APPEND, true, true);

                // Add the background image
                currentPageContentStream.drawImage(backgroundImage, 10, 10, bgWidth, bgHeight);

                // Draw text and tables on top of the background image as you were doing before

                currentPageContentStream.close();
            }

            document.save(outputStream);
            document.close();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "ClaimRecords.pdf");

            ByteArrayResource resource = new ByteArrayResource(outputStream.toByteArray());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(outputStream.size())
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    // Method to format date


    private Integer parseYear(String year) {
        if (year == null || year.isEmpty() || "all".equalsIgnoreCase(year)) {
            return null;
        }
        return Integer.parseInt(year);
    }

    private Integer parseMonth(String month) {
        if (month == null || month.isEmpty() || "all".equalsIgnoreCase(month)) {
            return null;
        }
        return Integer.parseInt(month);
    }

    private UUID parseStaffId(String staffId) {
        if (staffId == null || staffId.isEmpty() || "all".equalsIgnoreCase(staffId)) {
            return null;
        }
        try {
            return UUID.fromString(staffId);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Status parseStatus(String status) {
        if (status == null || status.isEmpty() || "all".equalsIgnoreCase(status)) {
            return null;
        }
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }




}
