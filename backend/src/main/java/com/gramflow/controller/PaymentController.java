package com.gramflow.controller;

import com.gramflow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-subscription")
    public ResponseEntity<Map<String, String>> createSubscription(Authentication authentication) {
        try {
            String subId = paymentService.createSubscriptionLink(authentication.getName());
            return ResponseEntity.ok(Map.of("subscriptionId", subId));
        } catch (Exception e) {
            log.error("Failed to create subscription for user {}", authentication.getName(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @org.springframework.beans.factory.annotation.Value("${razorpay.key.secret}")
    private String razorpaySecret;

    @PostMapping("/verify-subscription")
    public ResponseEntity<Map<String, Boolean>> verifySubscription(@RequestBody Map<String, String> payload) {
        try {
            String paymentId = payload.get("razorpayPaymentId");
            String subscriptionId = payload.get("razorpaySubscriptionId");
            String signature = payload.get("razorpaySignature");

            org.json.JSONObject options = new org.json.JSONObject();
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_subscription_id", subscriptionId);
            options.put("razorpay_signature", signature);

            boolean isSignatureValid = false;
            try {
                isSignatureValid = com.razorpay.Utils.verifyPaymentSignature(options, razorpaySecret);
            } catch (Exception e) {
                log.error("Signature verification failed", e);
            }

            if (isSignatureValid) {
                // Instantly upgrade them so they don't have to wait for the webhook
                paymentService.handleSubscriptionCharged(subscriptionId);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.status(400).body(Map.of("success", false));
            }
        } catch (Exception e) {
            log.error("Failed to verify signature", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
