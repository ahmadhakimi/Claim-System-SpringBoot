//StaffService.java
package com.project.claim.system.service;

import com.project.claim.system.auth.AuthenticationService;
import com.project.claim.system.dto.NewUserDTO;
import com.project.claim.system.dto.RegisterResponseDTO;
import com.project.claim.system.dto.StaffDTO;
import com.project.claim.system.entity.StaffEntity;
import com.project.claim.system.repository.StaffRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.internal.EntityState;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final JWTService jwtService;
    private final AuthenticationService authenticationService;
    private final JavaMailSender javaMailSender;

    //CREATE A NEW STAFF
    public StaffDTO createStaff(StaffDTO staffDTO) {
        StaffEntity staffEntity = convertToEntity(staffDTO);
        StaffEntity savedEntity = staffRepository.save(staffEntity);
        return convertToDTO(savedEntity);
    }



    //GET ALL STAFFS
    public List<StaffDTO> staffList() {
        List<StaffEntity> staffEntities = staffRepository.findAll();
        return staffEntities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // GET STAFF BY ID
    public StaffDTO viewStaff(UUID id) { // Change method signature to accept UUID
        StaffEntity staffEntity = getStaffEntityById(id);
        return convertToDTO(staffEntity);
    }

    // UPDATE STAFF
    public StaffDTO updateStaff(StaffDTO staffDTO, UUID id) { // Change method signature to accept UUID
        StaffEntity existingStaffEntity = getStaffEntityById(id);
        StaffEntity updatedStaffEntity = updateEntityFromDTO(existingStaffEntity, staffDTO);
        StaffEntity savedEntity = staffRepository.save(updatedStaffEntity);
        return convertToDTO(savedEntity);
    }

    // DELETE USER (SOFT DELETE)
    public void deleteStaff(UUID id) { // Change method signature to accept UUID
        StaffEntity staffEntity = getStaffEntityById(id);
        staffEntity.setDeleted(true);
        staffRepository.save(staffEntity);
    }

    //create new user by admin
    public NewUserDTO createdByAdmin(StaffDTO staffDTO) {
        // Generate a random password of length 8
        String randomPassword = generateRandomPassword(8);

        // Hash the random password
        String salt = authenticationService.generateSalt();
        String hashedPassword = authenticationService.hashPassword(randomPassword, salt);

        String hashedPasswordWithSalt = hashedPassword +"."+salt;

        // Create the entity from DTO
        StaffEntity staffEntity = convertToEntity(staffDTO);
        staffEntity.setPassword(hashedPasswordWithSalt);

        // Save the entity
        StaffEntity savedEntity = staffRepository.save(staffEntity);

        sendPasswordByEmail(savedEntity.getEmail(), randomPassword);

        // Create the response DTO
        NewUserDTO responseDTO = new NewUserDTO();
        responseDTO.setId(savedEntity.getId());
        responseDTO.setFullName(savedEntity.getFullName());
        responseDTO.setEmail(savedEntity.getEmail());
        responseDTO.setPassword(randomPassword);
        responseDTO.setRole(savedEntity.getRole());
        responseDTO.setCreatedAt(savedEntity.getCreatedAt());
        responseDTO.setUpdatedAt(savedEntity.getUpdatedAt());

        return responseDTO;
    }


    private StaffEntity getStaffEntityById(UUID id) {
        Optional<StaffEntity> optionalStaffEntity = staffRepository.findById(id);
        return optionalStaffEntity.orElseThrow(() -> new RuntimeException("Staff not found"));
    }


    //CONVERSION DTO & ENTITY
    private StaffDTO convertToDTO(StaffEntity staffEntity) {
        StaffDTO staffDTO = new StaffDTO();

        staffDTO.setId(String.valueOf(staffEntity.getId()));
        staffDTO.setFullName(staffEntity.getFullName());
        staffDTO.setEmail(staffEntity.getEmail());
        staffDTO.setPassword(staffEntity.getPassword());
        staffDTO.setRole(staffEntity.getRole());
        staffDTO.setCreatedBy(staffEntity.getCreatedBy());
        staffDTO.setUpdatedBy(staffEntity.getUpdatedBy());
        staffDTO.setDeleted(staffEntity.isDeleted());
        staffDTO.setCreatedAt(staffEntity.getCreatedAt());
        staffDTO.setUpdatedAt(staffEntity.getUpdatedAt());
        return staffDTO;
    }

    private StaffEntity convertToEntity(StaffDTO staffDTO) {
        StaffEntity staffEntity = new StaffEntity();
        staffEntity.setFullName(staffDTO.getFullName());
        staffEntity.setEmail(staffDTO.getEmail());
        staffEntity.setPassword(staffDTO.getPassword());
        staffEntity.setRole(staffDTO.getRole());
        staffEntity.setCreatedBy(staffDTO.getCreatedBy());
        staffEntity.setUpdatedBy(staffDTO.getUpdatedBy());
        staffEntity.setDeleted(staffDTO.isDeleted());
        staffEntity.setCreatedAt(staffDTO.getCreatedAt());
        staffEntity.setUpdatedAt(staffDTO.getUpdatedAt());

        return staffEntity;
    }

    private StaffEntity updateEntityFromDTO(StaffEntity staffEntity, StaffDTO staffDTO) {
        staffEntity.setFullName(staffDTO.getFullName());
        staffEntity.setEmail(staffDTO.getEmail());
        staffEntity.setPassword(staffDTO.getPassword());
        staffEntity.setRole(staffDTO.getRole());
        staffEntity.setUpdatedBy(staffDTO.getUpdatedBy());
        staffEntity.setDeleted(staffDTO.isDeleted());

        staffEntity.setUpdatedAt(new Date());
        return   staffEntity;
    }

    private String generateRandomPassword(int length) {
        // Define characters to be used for generating random password
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        // Generate a random password using SecureRandom
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            password.append(characters.charAt(index));
        }
        return password.toString();
    }

    private void sendPasswordByEmail(String newEmail, String password) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(newEmail);
            helper.setSubject("Your Account Information");
            helper.setText("Your account is created. Your password is: " + password);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
        javaMailSender.send(message);
    }


}
