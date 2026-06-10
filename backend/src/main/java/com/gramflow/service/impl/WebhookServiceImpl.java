package com.gramflow.service.impl;

import com.gramflow.entity.Automation;
import com.gramflow.entity.InstagramAccount;
import com.gramflow.entity.SubscriptionTier;
import com.gramflow.entity.User;
import com.gramflow.repository.AutomationRepository;
import com.gramflow.repository.InstagramAccountRepository;
import com.gramflow.service.InstagramService;
import com.gramflow.service.RateLimitService;
import com.gramflow.service.WebhookService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class WebhookServiceImpl implements WebhookService {

    private final InstagramAccountRepository instagramAccountRepository;
    private final AutomationRepository automationRepository;
    private final InstagramService instagramService;
    private final com.gramflow.repository.UserRepository userRepository;
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Async("webhookTaskExecutor")
    public void processWebhookEvent(String payload) {
        log.info("Processing webhook event asynchronously in thread: {}", Thread.currentThread().getName());
        try {
            JsonNode root = objectMapper.readTree(payload);
            String objectField = root.has("object") ? root.get("object").asText() : "";
            
            if (!"instagram".equals(objectField)) {
                log.warn("Received non-instagram webhook event: {}", objectField);
                return;
            }

            JsonNode entryNode = root.get("entry");
            if (entryNode == null || !entryNode.isArray()) {
                return;
            }

            for (JsonNode entry : entryNode) {
                String igAccountId = entry.has("id") ? entry.get("id").asText() : "";
                
                InstagramAccount account = instagramAccountRepository.findById(igAccountId).orElse(null);
                if (account == null) {
                    log.warn("Instagram Account ID {} not found in database. Skipping event.", igAccountId);
                    continue;
                }

                String userId = account.getUserId();
                JsonNode changesNode = entry.get("changes");
                if (changesNode == null || !changesNode.isArray()) {
                    continue;
                }

                for (JsonNode change : changesNode) {
                    String field = change.has("field") ? change.get("field").asText() : "";
                    if (!"comments".equals(field)) {
                        continue;
                    }

                    JsonNode valueNode = change.get("value");
                    if (valueNode == null) {
                        continue;
                    }

                    String commentId = valueNode.has("id") ? valueNode.get("id").asText() : "";
                    String commentText = valueNode.has("text") ? valueNode.get("text").asText() : "";
                    String mediaId = valueNode.has("media") && valueNode.get("media").has("id") 
                            ? valueNode.get("media").get("id").asText() 
                            : "";

                    log.info("Comment received on media {}: '{}' (ID: {})", mediaId, commentText, commentId);

                    if (commentId.isEmpty() || commentText.isEmpty() || mediaId.isEmpty()) {
                        continue;
                    }

                    List<Automation> automations = automationRepository
                            .findByInstagramAccountIdAndMediaIdAndActiveTrue(igAccountId, mediaId);

                    for (Automation automation : automations) {
                        String keyword = automation.getTriggerKeyword().toLowerCase();
                        if (commentText.toLowerCase().contains(keyword)) {
                            log.info("Matched trigger keyword '{}' for automation ID {}", keyword, automation.getId());

                            // Validate subscription limits at runtime
                            User user = userRepository.findById(userId).orElse(null);
                            if (user != null && user.getSubscriptionTier() == SubscriptionTier.FREE) {
                                List<Automation> userAutomations = automationRepository.findByUserId(user.getId());
                                List<Automation> activeAutos = userAutomations.stream()
                                        .filter(Automation::isActive)
                                        .toList();

                                if (activeAutos.size() > 1) {
                                    log.warn("FREE user {} has {} active automations. Limit is 1. Skipping execution.", user.getEmail(), activeAutos.size());
                                    continue;
                                }

                                if (activeAutos.isEmpty() || !activeAutos.get(0).getId().equals(automation.getId())) {
                                    log.warn("FREE user {} matched automation is not the single active. Skipping.", user.getEmail());
                                    continue;
                                }
                            }

                            // Meta Compliance: Check Rate Limit (50/min per account)
                            if (rateLimitService.resolveBucket(igAccountId).tryConsume(1)) {
                                instagramService.sendPrivateReply(
                                        commentId,
                                        automation.getReplyMessage(),
                                        account.getPageAccessToken()
                                );
                                
                                // Track real analytics
                                automation.setRepliesSent((automation.getRepliesSent() == null ? 0 : automation.getRepliesSent()) + 1);
                                automationRepository.save(automation);
                            } else {
                                log.warn("Rate limit exceeded for Instagram Account {}. Dropping reply to prevent Meta ban.", igAccountId);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error parsing webhook event payload", e);
        }
    }
}
