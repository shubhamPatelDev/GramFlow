package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.facade.AuthFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping("/firebase-login")
    public ResponseEntity<AuthResponse> firebaseLogin(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(authFacade.firebaseLogin(body.get("token")));
    }

    @GetMapping("/me")
    public ResponseEntity<com.example.demo.entity.User> getMe(Authentication authentication) {
        return ResponseEntity.ok(authFacade.getMe(authentication.getName()));
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Void> upgrade(Authentication authentication) {
        authFacade.upgrade(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/downgrade")
    public ResponseEntity<Void> downgrade(Authentication authentication) {
        authFacade.downgrade(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<Map<String, Object>> verifyEmail(@RequestParam("oobCode") String oobCode) {
        // Inform client to use Firebase client SDK for verification
        return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Please verify email using the Firebase client SDK."
        ));
    }
}
