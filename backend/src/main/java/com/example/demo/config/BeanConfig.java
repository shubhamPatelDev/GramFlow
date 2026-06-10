package com.example.demo.config;

import com.example.demo.facade.AuthFacade;
import com.example.demo.facade.AutomationFacade;
import com.example.demo.facade.InstagramFacade;
import com.example.demo.facade.WebhookFacade;
import com.example.demo.facade.impl.AuthFacadeImpl;
import com.example.demo.facade.impl.AutomationFacadeImpl;
import com.example.demo.facade.impl.InstagramFacadeImpl;
import com.example.demo.facade.impl.WebhookFacadeImpl;
import com.example.demo.mapper.AutomationMapper;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.AutomationRepository;
import com.example.demo.repository.InstagramAccountRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtils;
import com.example.demo.service.AutomationService;
import com.example.demo.service.InstagramService;
import com.example.demo.service.UserService;
import com.example.demo.service.WebhookService;
import com.example.demo.service.impl.AutomationServiceImpl;
import com.example.demo.service.impl.InstagramServiceImpl;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.service.impl.WebhookServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BeanConfig {

    // ==========================================
    // SERVICES BEANS
    // ==========================================

    @Bean
    public UserService userService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            AuthenticationManager authenticationManager,
            UserMapper userMapper
    ) {
        return new UserServiceImpl(userRepository, passwordEncoder, jwtUtils, authenticationManager, userMapper);
    }

    @Bean
    public InstagramService instagramService(InstagramAccountRepository instagramAccountRepository) {
        return new InstagramServiceImpl(instagramAccountRepository);
    }

    @Bean
    public AutomationService automationService(
            AutomationRepository automationRepository,
            InstagramAccountRepository instagramAccountRepository,
            AutomationMapper automationMapper
    ) {
        return new AutomationServiceImpl(automationRepository, instagramAccountRepository, automationMapper);
    }

    @Bean
    public WebhookService webhookService(
            InstagramAccountRepository instagramAccountRepository,
            AutomationRepository automationRepository,
            InstagramService instagramService,
            UserRepository userRepository,
            com.example.demo.service.RateLimitService rateLimitService) {
        return new WebhookServiceImpl(instagramAccountRepository, automationRepository, instagramService, userRepository, rateLimitService);
    }

    // ==========================================
    // FACADES BEANS
    // ==========================================

    @Bean
    public AuthFacade authFacade(UserService userService, JwtUtils jwtUtils, UserMapper userMapper) {
        return new AuthFacadeImpl(userService, jwtUtils, userMapper);
    }

    @Bean
    public InstagramFacade instagramFacade(InstagramService instagramService, UserService userService) {
        return new InstagramFacadeImpl(instagramService, userService);
    }

    @Bean
    public AutomationFacade automationFacade(AutomationService automationService, UserService userService) {
        return new AutomationFacadeImpl(automationService, userService);
    }

    @Bean
    public WebhookFacade webhookFacade(WebhookService webhookService) {
        return new WebhookFacadeImpl(webhookService);
    }
}
