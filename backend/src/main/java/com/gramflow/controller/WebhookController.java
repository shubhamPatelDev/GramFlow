package com.gramflow.controller;

import com.gramflow.facade.WebhookFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.gramflow.repository.InstagramAccountRepository;
import com.gramflow.repository.AutomationRepository;
import com.gramflow.entity.InstagramAccount;
import com.gramflow.entity.Automation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final WebhookFacade webhookFacade;
    private final InstagramAccountRepository instagramAccountRepository;
    private final AutomationRepository automationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.facebook.verify-token:insta_link_secret_token_2026}")
    private String verifyToken;

    @Value("${app.facebook.client-secret}")
    private String clientSecret;

    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(value = "hub.mode", required = false) String mode,
            @RequestParam(value = "hub.verify_token", required = false) String token,
            @RequestParam(value = "hub.challenge", required = false) String challenge
    ) {
        log.info("Received Facebook Webhook verification request. Mode: {}, Token: {}", mode, token);

        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            log.info("Webhook verification successful. Returning challenge: {}", challenge);
            return ResponseEntity.ok(challenge);
        } else {
            log.warn("Webhook verification failed. Mode or token mismatch.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification token mismatch");
        }
    }

    @PostMapping
    public ResponseEntity<Void> receiveWebhookEvent(@RequestBody String payload) {
        log.info("Received Facebook Webhook event payload");
        webhookFacade.receiveWebhookEvent(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/data-deletion")
    public ResponseEntity<Map<String, String>> handleDataDeletion(@RequestParam(value = "signed_request", required = false) String signedRequest) {
        log.warn("Received Data Deletion request from Meta.");
        if (signedRequest != null) {
            processSignedRequest(signedRequest);
        }
        
        String confirmationCode = UUID.randomUUID().toString();
        return ResponseEntity.ok(Map.of(
            "url", "https://commentor.example.com/deletion-status?id=" + confirmationCode,
            "confirmation_code", confirmationCode
        ));
    }

    @PostMapping("/deauthorize")
    public ResponseEntity<Void> handleDeauthorization(@RequestParam(value = "signed_request", required = false) String signedRequest) {
        log.warn("Received Deauthorization request from Meta.");
        if (signedRequest != null) {
            processSignedRequest(signedRequest);
        }
        return ResponseEntity.ok().build();
    }

    protected void processSignedRequest(String signedRequest) {
        try {
            String[] parts = signedRequest.split("\\.", 2);
            if (parts.length != 2) return;
            String encodedSig = parts[0];
            String payload = parts[1];

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(clientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] expectedSig = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            
            // Pad base64 if necessary
            String paddedSig = encodedSig.replace("-", "+").replace("_", "/");
            while (paddedSig.length() % 4 != 0) paddedSig += "=";
            byte[] providedSigBytes = Base64.getDecoder().decode(paddedSig);
            
            if (!java.util.Arrays.equals(expectedSig, providedSigBytes)) {
                log.error("Invalid signature on Meta request");
                return;
            }

            String decodedPayload = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            JsonNode payloadNode = objectMapper.readTree(decodedPayload);
            String fbUserId = payloadNode.has("user_id") ? payloadNode.get("user_id").asText() : null;

            if (fbUserId != null) {
                log.info("Valid request to purge Facebook User ID: {}", fbUserId);
                instagramAccountRepository.findByFacebookUserId(fbUserId).ifPresent(account -> {
                    List<Automation> automations = automationRepository.findByInstagramAccountId(account.getId());
                    automationRepository.deleteAll(automations);
                    instagramAccountRepository.delete(account);
                    log.info("Successfully deleted all data and rules for Facebook User ID: {}", fbUserId);
                });
            }
        } catch (Exception e) {
            log.error("Failed to process Meta signed request", e);
        }
    }
}
