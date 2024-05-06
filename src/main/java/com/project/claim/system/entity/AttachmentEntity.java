//AttachmentEntity.java

package com.project.claim.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Types;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "attachment")
@Data


public class AttachmentEntity {

    @Id
    @GeneratedValue
    private UUID id;
    private String name;

    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] data;
    private String type;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimId")
    private ClaimEntity claim;

}
