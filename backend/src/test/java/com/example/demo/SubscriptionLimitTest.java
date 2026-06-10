package com.example.demo;

import com.example.demo.dto.AutomationResponse;
import com.example.demo.dto.CreateAutomationRequest;
import com.example.demo.entity.Automation;
import com.example.demo.entity.InstagramAccount;
import com.example.demo.entity.SubscriptionTier;
import com.example.demo.entity.User;
import com.example.demo.exception.CustomException;
import com.example.demo.mapper.AutomationMapper;
import com.example.demo.repository.AutomationRepository;
import com.example.demo.repository.InstagramAccountRepository;
import com.example.demo.service.impl.AutomationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionLimitTest {

    @Mock
    private AutomationRepository automationRepository;

    @Mock
    private InstagramAccountRepository instagramAccountRepository;

    @Mock
    private AutomationMapper automationMapper;

    @InjectMocks
    private AutomationServiceImpl automationService;

    private User freeUser;
    private User paidUser;
    private InstagramAccount igAccount;

    @BeforeEach
    void setUp() {
        freeUser = User.builder()
                .id(UUID.randomUUID())
                .email("free@example.com")
                .subscriptionTier(SubscriptionTier.FREE)
                .build();

        paidUser = User.builder()
                .id(UUID.randomUUID())
                .email("paid@example.com")
                .subscriptionTier(SubscriptionTier.PAID)
                .build();

        igAccount = InstagramAccount.builder()
                .id("ig_123")
                .user(freeUser)
                .pageId("page_123")
                .pageName("Test Page")
                .pageAccessToken("encrypted_token")
                .instagramUsername("test_ig")
                .build();
    }

    @Test
    void createAutomation_FreeUserExceedsActiveLimit_ThrowsException() {
        when(instagramAccountRepository.findByUserId(freeUser.getId())).thenReturn(Optional.of(igAccount));
        when(automationRepository.countByUserIdAndActiveTrue(freeUser.getId())).thenReturn(1L);

        CreateAutomationRequest request = CreateAutomationRequest.builder()
                .mediaId("post_A")
                .mediaUrl("http://post.url")
                .mediaCaption("My Post")
                .triggerKeyword("link")
                .replyMessage("Here is your link!")
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> {
            automationService.createAutomation(freeUser, request);
        });

        assertEquals(HttpStatus.PAYMENT_REQUIRED, exception.getStatus());
        assertTrue(exception.getMessage().contains("Free tier is limited to 1 active automation"));
    }

    @Test
    void createAutomation_FreeUserExceedsPostLimit_ThrowsException() {
        when(instagramAccountRepository.findByUserId(freeUser.getId())).thenReturn(Optional.of(igAccount));
        when(automationRepository.countByUserIdAndActiveTrue(freeUser.getId())).thenReturn(0L);

        List<Automation> existing = List.of(
                Automation.builder()
                        .mediaId("post_B")
                        .active(false)
                        .build()
        );
        when(automationRepository.findByUserId(freeUser.getId())).thenReturn(existing);

        CreateAutomationRequest request = CreateAutomationRequest.builder()
                .mediaId("post_A")
                .mediaUrl("http://post.url")
                .mediaCaption("My Post")
                .triggerKeyword("link")
                .replyMessage("Here is your link!")
                .build();

        CustomException exception = assertThrows(CustomException.class, () -> {
            automationService.createAutomation(freeUser, request);
        });

        assertEquals(HttpStatus.PAYMENT_REQUIRED, exception.getStatus());
        assertTrue(exception.getMessage().contains("Free tier is limited to 1 post"));
    }

    @Test
    void createAutomation_PaidUserNoLimits_Success() {
        igAccount.setUser(paidUser);
        when(instagramAccountRepository.findByUserId(paidUser.getId())).thenReturn(Optional.of(igAccount));


        CreateAutomationRequest request = CreateAutomationRequest.builder()
                .mediaId("post_A")
                .mediaUrl("http://post.url")
                .mediaCaption("My Post")
                .triggerKeyword("link")
                .replyMessage("Here is your link!")
                .build();

        Automation automationEntity = Automation.builder()
                .mediaId("post_A")
                .triggerKeyword("link")
                .replyMessage("Here is your link!")
                .active(true)
                .build();

        when(automationMapper.createRequestToAutomation(any(CreateAutomationRequest.class)))
                .thenReturn(automationEntity);

        Automation expectedSave = Automation.builder()
                .id(UUID.randomUUID())
                .user(paidUser)
                .instagramAccount(igAccount)
                .mediaId("post_A")
                .triggerKeyword("link")
                .replyMessage("Here is your link!")
                .active(true)
                .build();

        when(automationRepository.save(any(Automation.class))).thenReturn(expectedSave);

        AutomationResponse response = AutomationResponse.builder()
                .id(expectedSave.getId())
                .mediaId("post_A")
                .triggerKeyword("link")
                .replyMessage("Here is your link!")
                .active(true)
                .build();

        when(automationMapper.automationToResponse(any(Automation.class))).thenReturn(response);

        assertNotNull(automationService.createAutomation(paidUser, request));
        verify(automationRepository, times(1)).save(any(Automation.class));
    }
}
