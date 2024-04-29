package com.project.claim.system.auth;


import com.project.claim.system.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String fullName;
    private String email;
    private String password;
    private Role role;

    //add createdBy and updatedBy
    private String createdBy;
    private String updatedBy;
}
