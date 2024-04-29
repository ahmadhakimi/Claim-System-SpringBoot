package com.project.claim.system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.claim.system.enumeration.Role;
import lombok.*;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder

public class NewUserDTO {

    private UUID id;
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    private Role role;
    private Date createdAt;
    private Date updatedAt;

}
