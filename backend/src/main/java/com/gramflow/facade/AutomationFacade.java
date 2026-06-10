package com.gramflow.facade;

import com.gramflow.dto.AutomationResponse;
import com.gramflow.dto.CreateAutomationRequest;

import java.util.List;
import java.util.UUID;

public interface AutomationFacade {
    AutomationResponse createAutomation(String email, CreateAutomationRequest request);
    List<AutomationResponse> getAutomations(String email);
    AutomationResponse toggleAutomation(String email, String id);
    void deleteAutomation(String email, String id);
    java.util.Map<String, Object> getStats(String email);
}
