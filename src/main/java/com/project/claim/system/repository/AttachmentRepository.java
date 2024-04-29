//AttachmentRepository.java

package com.project.claim.system.repository;

import com.project.claim.system.entity.AttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface AttachmentRepository extends JpaRepository<AttachmentEntity, UUID> {

    Optional<AttachmentEntity> findByClaimId(UUID claimId);
}
