package com.example.demo.service;

import com.example.demo.dto.AutomationResponse;
import com.example.demo.dto.CreateAutomationRequest;
import com.example.demo.entity.User;

import java.util.List;
import java.util.UUID;

public interface AutomationService {
    AutomationResponse createAutomation(User user, CreateAutomationRequest request);
    List<AutomationResponse> getAutomations(User user);
    AutomationResponse toggleAutomation(User user, String id);
    void deleteAutomation(User user, String id);
}
