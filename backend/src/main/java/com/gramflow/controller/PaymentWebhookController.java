package com.gramflow.controller;

import com.gramflow.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentService paymentService;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<Void> handleRazorpayWebhook(
            @RequestHeader("X-Razorpay-Signature") String signature,
            @RequestBody String payload) {
        try {
            boolean isSignatureValid = com.razorpay.Utils.verifyWebhookSignature(payload, signature, webhookSecret);
            if (!isSignatureValid) {
                log.error("Invalid Razorpay Webhook Signature");
                return ResponseEntity.status(403).build();
            }

            JSONObject event = new JSONObject(payload);
            String eventType = event.getString("event");
            log.info("Received Razorpay Webhook Event: {}", eventType);

            JSONObject payloadObj = event.getJSONObject("payload");

            if ("subscription.charged".equals(eventType)) {
                JSONObject subscription = payloadObj.getJSONObject("subscription").getJSONObject("entity");
                String subId = subscription.getString("id");
                paymentService.handleSubscriptionCharged(subId);
            } 
            else if ("subscription.cancelled".equals(eventType) || "subscription.halted".equals(eventType)) {
                JSONObject subscription = payloadObj.getJSONObject("subscription").getJSONObject("entity");
                String subId = subscription.getString("id");
                paymentService.handleSubscriptionCancelled(subId);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing Razorpay webhook", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
