package com.project.claim.system.auth;

import com.project.claim.system.entity.StaffEntity;
import com.project.claim.system.enumeration.Role;
import com.project.claim.system.exception.InvalidOTPException;
import com.project.claim.system.repository.StaffRepository;
import com.project.claim.system.service.EmailService;
import com.project.claim.system.service.JWTService;
import com.project.claim.system.service.OTPService;
import com.project.claim.system.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final StaffRepository staffRepository;
    private final OTPService otpService;
    private final EmailService emailService;
    private final JWTService jwtService;


    //REGISTRATION SERVICES
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(registerRequest.getPassword(), salt);

        String passwordWithSalt = hashedPassword + "." + salt;

        var staff = StaffEntity.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .password(passwordWithSalt)
                .role(registerRequest.getRole())
                .createdBy(registerRequest.getCreatedBy())
                .updatedBy(registerRequest.getUpdatedBy())
                .build();

        staffRepository.save(staff);
        var jwtToken = jwtService.generateToken(staff);
        return AuthenticationResponse.builder().token(jwtToken).build();

    }

    //AUTHENTICATION SERVICES
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var email = authenticationRequest.getEmail();
        var userDetails = staffRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        String fullName = userDetails.getFullName();
        Role role = userDetails.getRole();
        UUID id = userDetails.getId();
        String userEmail = userDetails.getEmail();

        String storedPassword = userDetails.getPassword();
        String storedHashedPassword = storedPassword.split("\\.")[0];
        String storedSalt = storedPassword.split("\\.")[1];

        String hashedPassword = hashPassword(authenticationRequest.getPassword(), storedSalt);

        // Verify password
        if (!hashedPassword.equals(storedHashedPassword)) {
            throw new BadCredentialsException("Invalid password");
        }

        var jwtToken = jwtService.generateToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .fullName(fullName)
                .email(userEmail)
                .id(id)
                .role(role)
                .build();
    }

    //CHANGE PASSWORD SERVICE
    public void changePassword(ChangePasswordRequest changePasswordRequest, String userEmail) {
        // Retrieve the user from the database
        StaffEntity user = staffRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the old password matches
        if (!verifyPassword(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        // Check if the new password and confirm password match
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // Generate new salt and hash the new password
        String salt = generateSalt();
        String hashedPassword = hashPassword(changePasswordRequest.getNewPassword(), salt);

        // Concatenate hashed password and salt
        String passwordWithSalt = hashedPassword + "." + salt;

        // Update the password
        user.setPassword(passwordWithSalt);
        staffRepository.save(user);
    }

    //forgot-password services
    public void processForgotPassword(String userEmail) {
        otpService.generateAndSendOTP(userEmail);
    }

    public void processVerifyOTP(String otp) {
        // Verify OTP
        boolean isOTPVerified = otpService.verifyOTP(otp);
        if (!isOTPVerified) {
            throw new InvalidOTPException("Invalid OTP");
        }
    }

    //reset-password services
    public void processResetPassword(String userEmail, ResetPasswordRequest resetPasswordRequest) {
        String newPassword = resetPasswordRequest.getNewPassword();
        String confirmPassword = resetPasswordRequest.getConfirmPassword();

        // Reset password
        resetPassword(userEmail, newPassword, confirmPassword);
    }
    private void resetPassword(String userEmail, String newPassword, String confirmPassword) {
        // Check if the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // Retrieve user from the database
        StaffEntity user = staffRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Generate new salt and hash the new password
        String salt = generateSalt();
        String hashedPassword = hashPassword(newPassword, salt);

        // Concatenate hashed password and salt
        String passwordWithSalt = hashedPassword + "." + salt;

        // Update the user's password
        user.setPassword(passwordWithSalt);
        staffRepository.save(user);
    }


    //HASHING THE PASSWORD
    public String hashPassword(String password, String salt) {
        try {
            // Concatenate salt and password
            String saltedPassword = salt + password;
            // Hash concatenated string using SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            // Convert hash to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    private boolean verifyPassword(String password, String storedPassword) {
        String storedHashedPassword = storedPassword.split("\\.")[0];
        String storedSalt = storedPassword.split("\\.")[1];
        String hashedPassword = hashPassword(password, storedSalt);
        return hashedPassword.equals(storedHashedPassword);
    }


    public String generateSalt() {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }


}