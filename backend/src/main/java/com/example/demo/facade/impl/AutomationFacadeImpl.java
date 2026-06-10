package com.example.demo.facade.impl;

import com.example.demo.dto.AutomationResponse;
import com.example.demo.dto.CreateAutomationRequest;
import com.example.demo.entity.User;
import com.example.demo.facade.AutomationFacade;
import com.example.demo.service.AutomationService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class AutomationFacadeImpl implements AutomationFacade {

    private final AutomationService automationService;
    private final UserService userService;

    @Override
    public AutomationResponse createAutomation(String email, CreateAutomationRequest request) {
        User user = userService.findByEmail(email);
        return automationService.createAutomation(user, request);
    }

    @Override
    public List<AutomationResponse> getAutomations(String email) {
        User user = userService.findByEmail(email);
        return automationService.getAutomations(user);
    }

    @Override
    public AutomationResponse toggleAutomation(String email, String id) {
        User user = userService.findByEmail(email);
        return automationService.toggleAutomation(user, id);
    }

    @Override
    public void deleteAutomation(String email, String id) {
        User user = userService.findByEmail(email);
        automationService.deleteAutomation(user, id);
    }

    @Override
    public java.util.Map<String, Object> getStats(String email) {
        User user = userService.findByEmail(email);
        List<AutomationResponse> automations = automationService.getAutomations(user);
        
        long totalAutomations = automations.size();
        long activeAutomations = automations.stream().filter(AutomationResponse::isActive).count();
        long totalReplies = automations.stream().mapToInt(a -> a.getRepliesSent() == null ? 0 : a.getRepliesSent()).sum();
        
        String topPerformingRule = "None";
        String highestEngagementPost = "None";
        int maxReplies = 0;
        
        for (AutomationResponse a : automations) {
            int replies = a.getRepliesSent() == null ? 0 : a.getRepliesSent();
            if (replies > maxReplies) {
                maxReplies = replies;
                topPerformingRule = a.getTriggerKeyword();
                highestEngagementPost = a.getMediaId() != null && !a.getMediaId().isEmpty() ? a.getMediaId() : "Any Post";
            }
        }
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalAutomations", totalAutomations);
        stats.put("activeAutomations", activeAutomations);
        stats.put("totalReplies", totalReplies);
        stats.put("topPerformingRule", topPerformingRule);
        stats.put("highestEngagementPost", highestEngagementPost);
        stats.put("maxReplies", maxReplies);
        
        return stats;
    }
}
