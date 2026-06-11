package com.gramflow.controller;

import com.gramflow.dto.SupportRequest;
import com.gramflow.entity.SupportTicket;
import com.gramflow.entity.User;
import com.gramflow.repository.SupportTicketRepository;
import com.gramflow.repository.UserRepository;
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
        String email = authentication.getName();
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SupportTicket ticket = SupportTicket.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .build();

        SupportTicket saved = supportTicketRepository.save(ticket);
        return ResponseEntity.ok(saved);
    }
}
