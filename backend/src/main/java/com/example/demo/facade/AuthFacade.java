package com.example.demo.facade;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.User;

public interface AuthFacade {
    AuthResponse firebaseLogin(String firebaseToken);
    void upgrade(String email);
    void downgrade(String email);
    User getMe(String email);
}
