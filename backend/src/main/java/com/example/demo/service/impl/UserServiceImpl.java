package com.example.demo.service.impl;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.SubscriptionTier;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Override
    
    public AuthResponse register(SignupRequest request) {
        log.info("Registering user: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email is already registered", HttpStatus.BAD_REQUEST);
        }
        User user = userMapper.signupRequestToUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setSubscriptionTier(SubscriptionTier.FREE);
        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateToken(savedUser.getEmail());
        return userMapper.userToAuthResponse(savedUser, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Logging in user: {}", request.getEmail());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new CustomException("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
        String token = jwtUtils.generateToken(user.getEmail());
        return userMapper.userToAuthResponse(user, token);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    
    public void upgradeToPaid(String userId) {
        log.info("Upgrading user: {}", userId);
        User user = findById(userId);
        user.setSubscriptionTier(SubscriptionTier.PAID);
        userRepository.save(user);
    }

    @Override
    
    public void downgradeToFree(String userId) {
        log.info("Downgrading user: {}", userId);
        User user = findById(userId);
        user.setSubscriptionTier(SubscriptionTier.FREE);
        userRepository.save(user);
    }

    @Override
    
    public User findOrCreateFirebaseUser(String email) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            log.info("Creating new user from Firebase: {}", email);
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // Dummy password
            newUser.setSubscriptionTier(SubscriptionTier.FREE);
            newUser.setEmailVerified(true); // Firebase verified
            return userRepository.save(newUser);
        });
    }
}
