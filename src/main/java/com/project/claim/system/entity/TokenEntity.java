//tokenEntity.java
package com.project.claim.system.entity;

import com.project.claim.system.enumeration.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table (name =  "token ")

public class TokenEntity {

    @Id
    @GeneratedValue

    @JdbcTypeCode(Types.VARCHAR)
    private Integer id;

    private String token;
    @Enumerated(EnumType.STRING)
    private TokenType type;
    private boolean expired;
    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private StaffEntity staff;


}
