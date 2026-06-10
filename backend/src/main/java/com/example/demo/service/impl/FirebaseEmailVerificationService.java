package com.example.demo.service.impl;

import com.example.demo.dto.EmailVerificationRequest;
import com.example.demo.dto.EmailVerificationResponse;
import com.example.demo.service.EmailVerificationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FirebaseEmailVerificationService implements EmailVerificationService {

    private final JavaMailSender mailSender;

    @Override
    public EmailVerificationResponse sendVerificationEmail(EmailVerificationRequest request) {
        try {
            // Generate Firebase email verification link
            String link = FirebaseAuth.getInstance().generateEmailVerificationLink(request.email());
            // Send email using JavaMailSender (Gmail SMTP configured via Spring properties)
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(request.email());
            helper.setSubject("Verify your email address");
            helper.setText("<p>Please verify your email by clicking the link below:</p>" +
                    "<a href='" + link + "'>Verify Email</a>", true);
            mailSender.send(mimeMessage);
            return new EmailVerificationResponse(true, "Verification email sent");
        } catch (FirebaseAuthException e) {
            return new EmailVerificationResponse(false, "Firebase error: " + e.getMessage());
        } catch (Exception e) {
            return new EmailVerificationResponse(false, "Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyToken(String token) {
        // Admin SDK does not support applyActionCode; verification must be done client‑side.
        // This method returns false to indicate server‑side verification is not applicable.
        return false;
    }
}
