package com.example.demo.facade;

import com.example.demo.dto.AutomationResponse;
import com.example.demo.dto.CreateAutomationRequest;

import java.util.List;
import java.util.UUID;

public interface AutomationFacade {
    AutomationResponse createAutomation(String email, CreateAutomationRequest request);
    List<AutomationResponse> getAutomations(String email);
    AutomationResponse toggleAutomation(String email, String id);
    void deleteAutomation(String email, String id);
    java.util.Map<String, Object> getStats(String email);
}
