package com.project.claim.system.dto;


import com.project.claim.system.enumeration.Role;
import lombok.*;
import org.springframework.context.support.BeanDefinitionDsl;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class RegisterResponseDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private Role role;
    private Date createdAt;
}
