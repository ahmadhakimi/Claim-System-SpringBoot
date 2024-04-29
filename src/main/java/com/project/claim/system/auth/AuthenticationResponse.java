package com.project.claim.system.auth;

import com.project.claim.system.entity.StaffEntity;
import com.project.claim.system.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    private String token;
    private String fullName;
    private String email;
    private Role role;
    private UUID id;

}
