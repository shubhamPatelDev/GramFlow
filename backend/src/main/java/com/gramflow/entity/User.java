package com.gramflow.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @DocumentId
    private String id;
    private String email;
    private String password; // Will be null for Google users
    
    @Builder.Default
    private boolean emailVerified = false;
    
    private String razorpayCustomerId;
    private String razorpaySubscriptionId;
    private String subscriptionStatus; // active, halted, cancelled
    private String verificationToken;
    private SubscriptionTier subscriptionTier;
    private LocalDateTime createdAt;
    
    // Subscription Limits
    private LocalDateTime trialEndsAt;
    private LocalDateTime subscriptionValidUntil;
}
