package com.gramflow.service;

import com.gramflow.dto.AuthResponse;
import com.gramflow.dto.LoginRequest;
import com.gramflow.dto.SignupRequest;
import com.gramflow.entity.User;

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
