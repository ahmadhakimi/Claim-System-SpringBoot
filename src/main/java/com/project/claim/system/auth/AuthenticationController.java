package com.project.claim.system.auth;

import com.project.claim.system.exception.UnauthenticatedException;
import com.project.claim.system.repository.StaffRepository;
import com.project.claim.system.service.JWTService;
import com.project.claim.system.service.StaffService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;


@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationService authenticationService;
    private final StaffService staffService;


    //registration controller
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // Delegate the registration process to the authentication service
        try {
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body("User successfully registered ");
        } catch (AuthenticationException exception) {
            String errorMessage = "The user is not authenticated!!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
        }

    }

    //authentication controller
    @PostMapping("/authenticate")
    @ResponseBody
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (UnauthenticatedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



    //    update password controller
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();

        // Change the password
        authenticationService.changePassword(changePasswordRequest, userEmail);

        return ResponseEntity.ok().build();
    }

//    forgot-password request controller
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authenticationService.processForgotPassword(String.valueOf(forgotPasswordRequest));
        return ResponseEntity.ok("An OTP has been sent to your email for password reset.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOTPRequest verifyOTPRequest){
        String otp = verifyOTPRequest.getOtp();
        authenticationService.processVerifyOTP(otp);
        return ResponseEntity.ok("The OTP is verified");
    }

    // Endpoint to reset the password using OTP

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("email") String userEmail, @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authenticationService.processResetPassword(userEmail, resetPasswordRequest);
        return ResponseEntity.ok("Password reset successful.");
    }

//    @GetMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//
//        if(auth != null) {
//            new SecurityContextLogoutHandler().logout(request, response, auth);
//        }
//        return ResponseEntity.ok("Logged out successfully");
//    }


}