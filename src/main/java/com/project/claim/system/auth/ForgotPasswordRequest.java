package com.project.claim.system.auth;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ForgotPasswordRequest {

    private String email;
}
