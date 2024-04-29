//StaffDTO.java
package com.project.claim.system.dto;

import com.project.claim.system.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StaffDTO {

    private String id;
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private String createdBy;
    private String updatedBy;
    private boolean deleted;
    private Date createdAt;
    private Date updatedAt;
}
