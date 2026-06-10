package com.example.demo.service;

import com.example.demo.dto.EmailVerificationRequest;
import com.example.demo.dto.EmailVerificationResponse;

public interface EmailVerificationService {
    /**
     * Initiates email verification for the given user email.
     * Generates a one‑time token, stores it in the user entity, and sends a verification email.
     *
     * @param request contains the email address to verify
     * @return response with status and token expiration info (do not expose token to caller)
     */
    EmailVerificationResponse sendVerificationEmail(EmailVerificationRequest request);

    /**
     * Verifies the token supplied by the user.
     *
     * @param token token received from the verification link
     * @return true if the token is valid and the user's email is marked verified
     */
    boolean verifyToken(String token);
}
