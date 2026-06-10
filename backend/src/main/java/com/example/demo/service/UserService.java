package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.User;

import java.util.UUID;

public interface UserService {
    AuthResponse register(SignupRequest request);
    AuthResponse login(LoginRequest request);
    User findByEmail(String email);
    User findById(String id);
    void upgradeToPaid(String userId);
    void downgradeToFree(String userId);
    User findOrCreateFirebaseUser(String email);
}
