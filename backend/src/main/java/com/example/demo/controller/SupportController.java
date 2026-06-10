package com.example.demo.controller;

import com.example.demo.dto.SupportRequest;
import com.example.demo.entity.SupportTicket;
import com.example.demo.entity.User;
import com.example.demo.repository.SupportTicketRepository;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class SupportController {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;

    @PostMapping("/tickets")
    public ResponseEntity<?> createTicket(@Valid @RequestBody SupportRequest request, Authentication authentication) {
        String userId = authentication.getName();
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = SupportTicket.builder()
                .userId(userId)
                .email(user.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .build();

        SupportTicket saved = supportTicketRepository.save(ticket);
        return ResponseEntity.ok(saved);
    }
}
