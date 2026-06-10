package com.example.demo.controller;

import com.example.demo.dto.AutomationResponse;
import com.example.demo.dto.CreateAutomationRequest;
import com.example.demo.facade.AutomationFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/automations")
@RequiredArgsConstructor
public class AutomationController {

    private final AutomationFacade automationFacade;

    @PostMapping
    public ResponseEntity<AutomationResponse> createAutomation(
            Authentication authentication,
            @Valid @RequestBody CreateAutomationRequest request
    ) {
        AutomationResponse response = automationFacade.createAutomation(authentication.getName(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AutomationResponse>> getAutomations(Authentication authentication) {
        return ResponseEntity.ok(automationFacade.getAutomations(authentication.getName()));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<AutomationResponse> toggleAutomation(
            Authentication authentication,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(automationFacade.toggleAutomation(authentication.getName(), id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutomation(
            Authentication authentication,
            @PathVariable String id
    ) {
        automationFacade.deleteAutomation(authentication.getName(), id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Object>> getStats(Authentication authentication) {
        return ResponseEntity.ok(automationFacade.getStats(authentication.getName()));
    }
}
