package com.project.claim.system.dto;


import lombok.Builder;

@Builder
public record EmailBody(String to, String subject, String text) {
}
