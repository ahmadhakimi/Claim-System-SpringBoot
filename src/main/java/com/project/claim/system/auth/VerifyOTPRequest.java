package com.project.claim.system.auth;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class VerifyOTPRequest {
    private String otp;
}
