package com.project.claim.system.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AttachmentDTO {

    private UUID id;
    private String name;
    private String type;
    private byte[] data;
}
