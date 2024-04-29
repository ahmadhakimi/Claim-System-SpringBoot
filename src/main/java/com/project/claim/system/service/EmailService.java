package com.project.claim.system.service;

import com.project.claim.system.dto.EmailBody;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOTPEmail(String userEmail, String otp) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(userEmail);
            helper.setSubject("Your OTP for Password Reset");
            helper.setText("Your OTP for password reset is: " + otp);
            mailSender.send(message);
        } catch (MessagingException e) {

            e.printStackTrace();
        }
    }
}
