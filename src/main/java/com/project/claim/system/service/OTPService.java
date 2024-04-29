package com.project.claim.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class OTPService {
    private final Map<String, OTPDetails> otpMap = new HashMap<>();
    private final Map<String, String> otpToUserEmailMap = new HashMap<>(); // Map to store OTPs and associated user emails
    private final EmailService emailService;

    @Autowired
    public OTPService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void generateAndSendOTP(String userEmail) {
        // Generate OTP
        String otp = generateOTP();

        // Store OTP along with user email and expiration time
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5); // OTP expires in 5 minutes
        otpMap.put(otp, new OTPDetails(userEmail, expirationTime));
        otpToUserEmailMap.put(otp, userEmail);

        // Send OTP to user's email
        emailService.sendOTPEmail(userEmail, otp);
    }

    public boolean verifyOTP(String otp) {
        // Retrieve OTP details from the map
        OTPDetails otpDetails = otpMap.get(otp);
        if (otpDetails == null || otpDetails.getExpirationTime().isBefore(LocalDateTime.now())) {
            return false; // OTP doesn't exist or has expired
        }

        // OTP is valid
        otpMap.remove(otp); // Remove OTP from map after verification
        otpToUserEmailMap.remove(otp); // Remove OTP from the OTP to user email map
        return true;
    }

    public String getUserEmailForOTP(String otp) {
        // Retrieve user email associated with the OTP
        return otpToUserEmailMap.get(otp);
    }

    private String generateOTP() {
        // Generate a random OTP of length 6
        SecureRandom random = new SecureRandom();
        int otpLength = 6;
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10)); // Append random digits (0-9)
        }
        return otp.toString();
    }

    public class OTPDetails {
        private final String userEmail;
        private final LocalDateTime expirationTime;

        public OTPDetails(String userEmail, LocalDateTime expirationTime) {
            this.userEmail = userEmail;
            this.expirationTime = expirationTime;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }
    }
}
