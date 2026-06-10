package com.example.demo.controller;

import com.example.demo.dto.CreateOrderRequest;
import com.example.demo.dto.CreateOrderResponse;
import com.example.demo.dto.CreateSubscriptionResponse;
import com.example.demo.dto.VerifyPaymentRequest;
import com.example.demo.dto.VerifySubscriptionRequest;
import com.example.demo.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(razorpayService.createOrder(request));
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<String> verifyPayment(
            Authentication authentication,
            @RequestBody VerifyPaymentRequest request) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        razorpayService.verifyPayment(authentication.getName(), request);
        return ResponseEntity.ok("Payment verified successfully");
    }

    @PostMapping("/create-subscription")
    public ResponseEntity<CreateSubscriptionResponse> createSubscription(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(razorpayService.createSubscription(authentication.getName()));
    }

    @PostMapping("/verify-subscription")
    public ResponseEntity<String> verifySubscription(
            Authentication authentication,
            @RequestBody VerifySubscriptionRequest request) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        
        razorpayService.verifySubscription(authentication.getName(), request);
        return ResponseEntity.ok("Subscription verified successfully");
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        razorpayService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }
}
