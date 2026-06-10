package com.gramflow.config;

import com.gramflow.facade.AuthFacade;
import com.gramflow.facade.AutomationFacade;
import com.gramflow.facade.InstagramFacade;
import com.gramflow.facade.WebhookFacade;
import com.gramflow.facade.impl.AuthFacadeImpl;
import com.gramflow.facade.impl.AutomationFacadeImpl;
import com.gramflow.facade.impl.InstagramFacadeImpl;
import com.gramflow.facade.impl.WebhookFacadeImpl;
import com.gramflow.mapper.AutomationMapper;
import com.gramflow.mapper.UserMapper;
import com.gramflow.repository.AutomationRepository;
import com.gramflow.repository.InstagramAccountRepository;
import com.gramflow.repository.UserRepository;
import com.gramflow.security.JwtUtils;
import com.gramflow.service.AutomationService;
import com.gramflow.service.InstagramService;
import com.gramflow.service.UserService;
import com.gramflow.service.WebhookService;
import com.gramflow.service.impl.AutomationServiceImpl;
import com.gramflow.service.impl.InstagramServiceImpl;
import com.gramflow.service.impl.UserServiceImpl;
import com.gramflow.service.impl.WebhookServiceImpl;
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
            com.gramflow.service.RateLimitService rateLimitService) {
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
