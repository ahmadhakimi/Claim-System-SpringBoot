package com.project.claim.system.service;

import com.project.claim.system.dto.AttachmentDTO;
import com.project.claim.system.dto.ClaimDTO;
import com.project.claim.system.entity.AttachmentEntity;
import com.project.claim.system.entity.ClaimEntity;
import com.project.claim.system.entity.StaffEntity;
import com.project.claim.system.enumeration.Status;
import com.project.claim.system.exception.ResourceNotFoundException;
import com.project.claim.system.repository.AttachmentRepository;
import com.project.claim.system.repository.ClaimRepository;
import com.project.claim.system.repository.StaffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final StaffRepository staffRepository;
    private final ClaimRepository claimRepository;
    private final AttachmentRepository attachmentRepository;


    // CREATE CLAIM WITH ATTACHMENT
    public ClaimDTO createClaimWithAttachment(ClaimDTO claimDTO, MultipartFile attachmentFile) {
        StaffEntity staffEntity = staffRepository.findById(claimDTO.getStaffId()).orElse(null);
        if (staffEntity == null) {

            System.err.println("Invalid staffId provided: " + claimDTO.getStaffId());
            return null;
        }

        // Convert DTO to entity
        ClaimEntity claimEntity = convertToEntity(claimDTO);
        claimEntity.setStaff(staffEntity);

        // Save claim entity
        ClaimEntity savedClaimEntity = claimRepository.save(claimEntity);

        // Process attachment if available
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            try {
                // Create attachment entity
                AttachmentEntity attachmentEntity = new AttachmentEntity();
                attachmentEntity.setName(attachmentFile.getOriginalFilename());
                attachmentEntity.setData(attachmentFile.getBytes());
                attachmentEntity.setType(attachmentFile.getContentType());
                attachmentEntity.setClaim(savedClaimEntity);
                // Save attachment entity
                attachmentRepository.save(attachmentEntity);
                // Update attachment details in DTO
                claimDTO.setAttachmentId(attachmentEntity.getId());
                claimDTO.setAttachmentName(attachmentEntity.getName());
                claimDTO.setAttachmentType(attachmentEntity.getType());
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        // Set staff details in DTO
        claimDTO.setStaffId(staffEntity.getId());
        claimDTO.setFullName(staffEntity.getFullName());

        // Set the ID of the saved claim entity in the DTO
        claimDTO.setId(savedClaimEntity.getId());

        return claimDTO;
    }


    //GET CLAIM LIST BY PARAMS

    public List<ClaimDTO> getClaimsByParams(Integer year, Integer month, UUID staffId, Status status) {
        List<ClaimEntity> claimEntities = claimRepository.findAllByFilter(year, month, staffId, status);

        // Fetch staff details in bulk
        List<UUID> staffIds = claimEntities.stream()
                .map(claimEntity -> claimEntity.getStaff() != null ? claimEntity.getStaff().getId() : null)
                .collect(Collectors.toList());
        Map<UUID, StaffEntity> staffMap = staffRepository.findAllById(staffIds).stream()
                .collect(Collectors.toMap(StaffEntity::getId, Function.identity()));

        // Convert claim entities to DTOs
        List<ClaimDTO> claimDTOs = claimEntities.stream()
                .map(claimEntity -> {
                    ClaimDTO claimDTO = convertToDTO(claimEntity);
                    if (claimEntity.getStaff() != null) {
                        StaffEntity staffEntity = staffMap.get(claimEntity.getStaff().getId());
                        if (staffEntity != null) {
                            claimDTO.setStaffId(staffEntity.getId());
                            claimDTO.setFullName(staffEntity.getFullName());
                        }
                    }
                    return claimDTO;
                })
                .collect(Collectors.toList());

        return claimDTOs;
    }

    // EDIT CLAIM AND UPDATE ATTACHMENT
    @Transactional
    public ClaimDTO updateClaimWithAttachment(UUID id, ClaimDTO updatedClaimDTO, MultipartFile attachmentFile) {
        // Check if the claim with the given ID exists
        ClaimEntity existingClaimEntity = claimRepository.findById(id).orElse(null);
        if (existingClaimEntity == null) {
            return null; // Claim not found
        }

        // Update the existing claim entity with the values from updatedClaimDTO
        existingClaimEntity.setName(updatedClaimDTO.getName());
        existingClaimEntity.setDescription(updatedClaimDTO.getDescription());
        existingClaimEntity.setAmount(updatedClaimDTO.getAmount());
        existingClaimEntity.setReceiptNo(updatedClaimDTO.getReceiptNo());
        existingClaimEntity.setReceiptDate(updatedClaimDTO.getReceiptDate());
        existingClaimEntity.setStatus(updatedClaimDTO.getStatus());

        // Set the updatedAt timestamp to the current date and time
        existingClaimEntity.setUpdatedAt(new Date());

        // Fetch staff details from the database based on the provided staffId in the updatedClaimDTO
        StaffEntity staffEntity = staffRepository.findById(updatedClaimDTO.getStaffId()).orElse(null);
        if (staffEntity == null) {
            System.err.println("Invalid staffId provided: " + updatedClaimDTO.getStaffId());
            return null;
        }

        // Set the staff for the existing claim entity
        existingClaimEntity.setStaff(staffEntity);

        // Process attachment if available
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            try {
                // Fetch existing attachment entity
                Optional<AttachmentEntity> optionalAttachmentEntity = attachmentRepository.findByClaimId(id);
                if (optionalAttachmentEntity.isPresent()) {
                    AttachmentEntity existingAttachmentEntity = optionalAttachmentEntity.get();

                    // Update attachment details
                    existingAttachmentEntity.setName(attachmentFile.getOriginalFilename());
                    existingAttachmentEntity.setData(attachmentFile.getBytes());
                    existingAttachmentEntity.setType(attachmentFile.getContentType());

                    // Set the updatedAt timestamp to the current date and time
                    existingAttachmentEntity.setUpdatedAt(new Date());

                    // Save the updated attachment entity
                    attachmentRepository.save(existingAttachmentEntity);

                    // Update attachment details in DTO
                    updatedClaimDTO.setAttachmentId(existingAttachmentEntity.getId());
                    updatedClaimDTO.setAttachmentName(existingAttachmentEntity.getName());
                    updatedClaimDTO.setAttachmentType(existingAttachmentEntity.getType());
                }
            } catch (IOException e) {
                // Handle IOException appropriately
                e.printStackTrace();
            }
        }

        // Save the updated claim entity
        ClaimEntity savedUpdatedClaimEntity = claimRepository.save(existingClaimEntity);

        // Set staff details in DTO
        updatedClaimDTO.setStaffId(staffEntity.getId());
        updatedClaimDTO.setFullName(staffEntity.getFullName());

        // Set the ID of the saved updated claim entity in the DTO
        updatedClaimDTO.setId(savedUpdatedClaimEntity.getId());

        return updatedClaimDTO;
    }



//make details to appear in request
    private ClaimEntity convertToEntity(ClaimDTO claimDTO) {
        ClaimEntity claimEntity = new ClaimEntity();

        if(claimDTO.getId()!= null) {
            Optional<ClaimEntity> optionalClaimEntity = claimRepository.findById(claimDTO.getId());
            if(optionalClaimEntity.isPresent()) {
                ClaimEntity existingClaimEntity = optionalClaimEntity.get();
                claimEntity.setCreatedAt(existingClaimEntity.getCreatedAt());
            }
        }

        claimEntity.setName(claimDTO.getName());
        claimEntity.setDescription(claimDTO.getDescription());
        claimEntity.setAmount(claimDTO.getAmount());
        claimEntity.setReceiptNo(claimDTO.getReceiptNo());
        claimEntity.setReceiptDate(claimDTO.getReceiptDate());
        claimEntity.setStatus(claimDTO.getStatus());

        return claimEntity;
    }

//appear the details in the response
    private ClaimDTO convertToDTO(ClaimEntity claimEntity) {
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setId(claimEntity.getId());
        claimDTO.setName(claimEntity.getName());
        claimDTO.setDescription(claimEntity.getDescription());
        claimDTO.setAmount(claimEntity.getAmount());
        claimDTO.setReceiptNo(claimEntity.getReceiptNo());
        claimDTO.setReceiptDate(claimEntity.getReceiptDate());
        claimDTO.setStatus(claimEntity.getStatus());

        if(claimEntity.getAttachment() != null) {
            claimDTO.setAttachmentId(claimEntity.getAttachment().getId());
            claimDTO.setAttachmentName(claimEntity.getAttachment().getName());
            claimDTO.setAttachmentType(claimEntity.getAttachment().getType());

        }

        return claimDTO;
    }


    public UUID getAttachmentIdByClaimId(UUID id) {
        Optional<ClaimEntity> optional = claimRepository.findById(id);
        if(optional.isPresent()){
            ClaimEntity claim = optional.get();
            return claim.getAttachment().getId();
        } else {
            throw new ResourceNotFoundException("Claim Not Found! ");
        }
    }
}