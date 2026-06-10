package com.example.demo.facade.impl;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.User;
import com.example.demo.facade.AuthFacade;
import com.example.demo.service.UserService;
import com.example.demo.security.JwtUtils;
import com.example.demo.mapper.UserMapper;
import com.example.demo.exception.CustomException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthFacadeImpl implements AuthFacade {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;

    @Override
    public AuthResponse firebaseLogin(String firebaseToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String email = decodedToken.getEmail();
            if (email == null) {
                throw new CustomException("Email not found in Firebase token", HttpStatus.BAD_REQUEST);
            }
            User user = userService.findOrCreateFirebaseUser(email);
            String token = jwtUtils.generateToken(user.getEmail());
            return userMapper.userToAuthResponse(user, token);
        } catch (Exception e) {
            System.err.println("FIREBASE LOGIN FAILED:");
            e.printStackTrace();
            throw new CustomException("Invalid Firebase token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public void upgrade(String email) {
        User user = userService.findByEmail(email);
        userService.upgradeToPaid(user.getId());
    }

    @Override
    public void downgrade(String email) {
        User user = userService.findByEmail(email);
        userService.downgradeToFree(user.getId());
    }

    @Override
    public User getMe(String email) {
        return userService.findByEmail(email);
    }
}
