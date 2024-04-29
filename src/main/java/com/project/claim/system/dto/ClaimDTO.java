package com.project.claim.system.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.claim.system.enumeration.Name;
import com.project.claim.system.enumeration.Status;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO {

    private UUID id;
    private Name name;
    private String description;
    private BigDecimal amount;
    private String receiptNo;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date receiptDate;

    private Status status;
    private UUID staffId;
    private String fullName;

    private UUID attachmentId; // New field for attachment ID
    private String attachmentName; // New field for attachment name
    private String attachmentType;

    @JsonIgnore
    private MultipartFile attachmentFile;
}