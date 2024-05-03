// ClaimEntity.java
package com.project.claim.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.claim.system.enumeration.Name;
import com.project.claim.system.enumeration.Status;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "claim")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Name name;
    private String description;
    private BigDecimal amount;
    private String receiptNo;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date receiptDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staffId")
    private StaffEntity staff;

    private String createdBy;
    private String updatedBy;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @JsonIgnore
    @Fetch(FetchMode.JOIN)
    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL)
    private AttachmentEntity attachment;



}