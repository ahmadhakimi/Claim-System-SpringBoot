package com.project.claim.system.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ResetPasswordRequest {

    private String newPassword;
    private String confirmPassword;
}
