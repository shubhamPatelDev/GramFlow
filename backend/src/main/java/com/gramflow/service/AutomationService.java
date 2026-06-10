package com.gramflow.service;

import com.gramflow.dto.AutomationResponse;
import com.gramflow.dto.CreateAutomationRequest;
import com.gramflow.entity.User;

import java.util.List;
import java.util.UUID;

public interface AutomationService {
    AutomationResponse createAutomation(User user, CreateAutomationRequest request);
    List<AutomationResponse> getAutomations(User user);
    AutomationResponse toggleAutomation(User user, String id);
    void deleteAutomation(User user, String id);
}
