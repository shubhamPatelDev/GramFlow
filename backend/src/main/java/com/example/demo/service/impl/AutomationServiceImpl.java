package com.example.demo.service.impl;

import com.example.demo.dto.AutomationResponse;
import com.example.demo.dto.CreateAutomationRequest;
import com.example.demo.entity.Automation;
import com.example.demo.entity.InstagramAccount;
import com.example.demo.entity.SubscriptionTier;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.mapper.AutomationMapper;
import com.example.demo.repository.AutomationRepository;
import com.example.demo.repository.InstagramAccountRepository;
import com.example.demo.service.AutomationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class AutomationServiceImpl implements AutomationService {

    private final AutomationRepository automationRepository;
    private final InstagramAccountRepository instagramAccountRepository;
    private final AutomationMapper automationMapper;

    @Override
    
    public AutomationResponse createAutomation(User user, CreateAutomationRequest request) {
        log.info("Creating automation for user: {}, mediaId: {}", user.getEmail(), request.getMediaId());

        InstagramAccount account = instagramAccountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new CustomException("Please connect your Instagram account first", HttpStatus.BAD_REQUEST));

        // Enforce Subscription Limits
        if (user.getSubscriptionTier() == SubscriptionTier.FREE) {
            long activeCount = automationRepository.countByUserIdAndActiveTrue(user.getId());
            if (activeCount >= 1) {
                throw new CustomException("Free tier is limited to 1 active automation. Upgrade to PAID for unlimited automations.", HttpStatus.PAYMENT_REQUIRED);
            }

            List<Automation> existingAutomations = automationRepository.findByUserId(user.getId());
            boolean hasDifferentPostConfigured = existingAutomations.stream()
                    .anyMatch(auto -> !auto.getMediaId().equals(request.getMediaId()));

            if (hasDifferentPostConfigured) {
                throw new CustomException("Free tier is limited to 1 post. Delete existing automations for other posts to configure this one, or upgrade to PAID.", HttpStatus.PAYMENT_REQUIRED);
            }
        }

        Automation automation = automationMapper.createRequestToAutomation(request);
        automation.setUserId(user.getId());
        automation.setInstagramAccountId(account.getId());
        automation.setTriggerKeyword(request.getTriggerKeyword().trim().toLowerCase());
        automation.setActive(true);

        Automation saved = automationRepository.save(automation);
        return automationMapper.automationToResponse(saved);
    }

    @Override
    public List<AutomationResponse> getAutomations(User user) {
        return automationRepository.findByUserId(user.getId()).stream()
                .map(automationMapper::automationToResponse)
                .collect(Collectors.toList());
    }

    @Override
    
    public AutomationResponse toggleAutomation(User user, String id) {
        Automation automation = automationRepository.findById(id)
                .orElseThrow(() -> new CustomException("Automation not found", HttpStatus.NOT_FOUND));

        if (!automation.getUserId().equals(user.getId())) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        if (!automation.isActive() && user.getSubscriptionTier() == SubscriptionTier.FREE) {
            long activeCount = automationRepository.countByUserIdAndActiveTrue(user.getId());
            if (activeCount >= 1) {
                throw new CustomException("Free tier is limited to 1 active automation. Upgrade to PAID for unlimited automations.", HttpStatus.PAYMENT_REQUIRED);
            }
            
            List<Automation> existing = automationRepository.findByUserId(user.getId());
            boolean hasDifferentPostActive = existing.stream()
                    .anyMatch(auto -> auto.isActive() && !auto.getMediaId().equals(automation.getMediaId()));
            if (hasDifferentPostActive) {
                throw new CustomException("Free tier is limited to 1 active post. Upgrade to PAID for unlimited posts.", HttpStatus.PAYMENT_REQUIRED);
            }
        }

        automation.setActive(!automation.isActive());
        Automation saved = automationRepository.save(automation);
        return automationMapper.automationToResponse(saved);
    }

    @Override
    
    public void deleteAutomation(User user, String id) {
        Automation automation = automationRepository.findById(id)
                .orElseThrow(() -> new CustomException("Automation not found", HttpStatus.NOT_FOUND));

        if (!automation.getUserId().equals(user.getId())) {
            throw new CustomException("Access denied", HttpStatus.FORBIDDEN);
        }

        automationRepository.deleteById(automation.getId());
    }
}
