//TokenEntity.java
package com.project.claim.system.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "token")
@Data
public class TokenEntity {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private StaffEntity staff;

    private LocalDateTime expiryDatetime;
}
