package com.gramflow.facade;

import com.gramflow.dto.AuthResponse;
import com.gramflow.dto.LoginRequest;
import com.gramflow.dto.SignupRequest;
import com.gramflow.entity.User;

public interface AuthFacade {
    AuthResponse firebaseLogin(String firebaseToken);
    void upgrade(String email);
    void downgrade(String email);
    User getMe(String email);
}
